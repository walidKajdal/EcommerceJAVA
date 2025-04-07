import React, { useState, useEffect, useRef } from "react";
import { useParams, useNavigate } from "react-router-dom";

const TrackingPage = () => {
    const { orderId } = useParams();
    const navigate = useNavigate();
    const [socket, setSocket] = useState(null);
    const [message, setMessage] = useState("");
    const [messages, setMessages] = useState([]);
    const [status, setStatus] = useState("Preparing your order");
    const [location, setLocation] = useState("Store location");
    const [isConnected, setIsConnected] = useState(false);
    const socketRef = useRef(null);

    const connectWebSocket = () => {
        const ws = new WebSocket(`ws://localhost:8080/ws/order/${orderId}`);

        ws.onopen = () => {
            console.log("WebSocket connected");
            setIsConnected(true);
            setSocket(ws);
            socketRef.current = ws;
            ws.send(JSON.stringify({ role: "user" }));
        };

        ws.onmessage = (event) => {
            try {
                const data = JSON.parse(event.data);
                if (Array.isArray(data)) {
                    setMessages(data.map(msg => ({
                        text: msg.content,
                        sender: msg.sender,
                        timestamp: msg.timestamp
                    })));
                } else if (data.type === "status_update") {
                    setStatus(data.message);
                } else if (data.type === "location_update") {
                    setLocation(data.message);
                } else {
                    setMessages(prev => [...prev, {
                        text: data.content || data.message,
                        sender: data.sender || "delivery",
                        timestamp: data.timestamp || new Date().toISOString()
                    }]);
                }
            } catch (error) {
                console.error("Error parsing message:", error);
            }
        };

        ws.onclose = (event) => {
            console.log(`WebSocket closed: ${event.code} ${event.reason}`);
            setIsConnected(false);
            if (event.code !== 1000) {
                console.log("Attempting to reconnect...");
                setTimeout(connectWebSocket, 3000);
            }
        };

        ws.onerror = (error) => {
            console.error("WebSocket error:", error);
            setIsConnected(false);
        };
    };

    useEffect(() => {
        connectWebSocket();
        return () => {
            if (socketRef.current?.readyState === WebSocket.OPEN) {
                socketRef.current.close(1000, "Component unmounted");
            }
        };
    }, [orderId]);

    const sendMessage = () => {
        if (socket && message.trim()) {
            socket.send(JSON.stringify({ type: "chat_message", message }));
            setMessages((prev) => [...prev, {
                text: message,
                sender: "user",
                timestamp: new Date().toISOString()
            }]);
            setMessage("");
        }
    };

    return (
        <div className="max-w-2xl mx-auto p-4">
            <button
                onClick={() => navigate(-1)}
                className="text-blue-500 hover:underline mb-4"
            >
                ← Back
            </button>
            <h1 className="text-2xl font-semibold mt-2">Order Tracking - #{orderId}</h1>
            <div className={`inline-block px-2 py-1 rounded text-sm ${isConnected ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
                {isConnected ? 'Connected' : 'Disconnected'}
            </div>
            <p className="mt-2 text-gray-700"><strong>Status:</strong> {status}</p>
            <p className="text-gray-700"><strong>Location:</strong> {location}</p>
            <div className="mt-4 border rounded-lg p-4 bg-gray-100">
                <h2 className="text-lg font-semibold">Chat</h2>
                <div className="h-48 overflow-y-auto border p-2 bg-white rounded">
                    {messages.length === 0 ? (
                        <p className="text-gray-500 text-center py-4">No messages yet</p>
                    ) : (
                        messages.map((msg, index) => (
                            <div key={index} className={`mb-2 ${msg.sender === "user" ? 'text-right' : 'text-left'}`}>
                                <p className={`inline-block p-2 rounded-lg ${msg.sender === "user" ? 'bg-blue-100 text-blue-800' : 'bg-gray-200 text-gray-800'}`}>
                                    <strong>{msg.sender === "user" ? "You: " : "Delivery: "}</strong>
                                    {msg.text}
                                </p>
                                <p className="text-xs text-gray-500 mt-1">
                                    {new Date(msg.timestamp).toLocaleTimeString()}
                                </p>
                            </div>
                        ))
                    )}
                </div>
                <div className="flex mt-2">
                    <input
                        type="text"
                        value={message}
                        onChange={(e) => setMessage(e.target.value)}
                        onKeyDown={(e) => e.key === "Enter" && sendMessage()}
                        className="flex-1 p-2 border rounded-l-md"
                        placeholder="Type a message..."
                        disabled={!isConnected}
                    />
                    <button
                        onClick={sendMessage}
                        className="bg-black text-white px-4 py-2 rounded-r-md hover:bg-gray-800 disabled:bg-gray-400"
                        disabled={!isConnected || !message.trim()}
                    >
                        Send
                    </button>
                </div>
            </div>
        </div>
    );
};

export default TrackingPage;
