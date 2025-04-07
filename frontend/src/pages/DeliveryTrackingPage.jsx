import React, { useState, useEffect, useRef } from "react";
import { useParams } from "react-router-dom";

const DeliveryStatus = {
    PENDING: "Pending",
    SHIPPED: "Shipped",
    DELIVERED: "Delivered",
};

const DeliveryTrackingPage = () => {
    const { orderId } = useParams();
    const [socket, setSocket] = useState(null);
    const [message, setMessage] = useState("");
    const [messages, setMessages] = useState([]);
    const [status, setStatus] = useState(DeliveryStatus.PENDING);
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
            ws.send(JSON.stringify({ role: "delivery" }));
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
                        sender: data.sender || "user",
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
                setTimeout(connectWebSocket, 3000);
            }
        };

        ws.onerror = (error) => {
            console.error("WebSocket error:", error);
            setIsConnected(false);
        };

        return ws;
    };

    useEffect(() => {
        const ws = connectWebSocket();

        return () => {
            if (ws.readyState === WebSocket.OPEN) {
                ws.close(1000, "Component unmounted");
            }
        };
    }, [orderId]);

    const sendMessage = () => {
        if (socket && message.trim()) {
            socket.send(JSON.stringify({ type: "chat_message", message }));
            setMessages((prev) => [...prev, {
                text: message,
                sender: "delivery",
                timestamp: new Date().toISOString()
            }]);
            setMessage("");
        }
    };

    const updateStatus = (newStatus) => {
        setStatus(newStatus);
        if (socket) {
            socket.send(JSON.stringify({ type: "status_update", message: newStatus }));
        }
    };

    const updateLocation = () => {
        if (socket) {
            socket.send(JSON.stringify({ type: "location_update", message: location }));
        }
    };

    return (
        <div className="max-w-2xl mx-auto p-4">
            <h1 className="text-2xl font-semibold mt-2">Order Tracking - #{orderId}</h1>
            <div className={`inline-block px-2 py-1 rounded text-sm ${isConnected ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
                {isConnected ? 'Connected' : 'Disconnected'}
            </div>

            <div className="mt-4">
                <label className="block text-gray-700 font-semibold">Update Order Status:</label>
                <select
                    value={status}
                    onChange={(e) => updateStatus(e.target.value)}
                    className="w-full p-2 border rounded-md bg-white"
                >
                    {Object.values(DeliveryStatus).map((statusOption) => (
                        <option key={statusOption} value={statusOption}>
                            {statusOption}
                        </option>
                    ))}
                </select>
            </div>

            {/* Location Input */}
            <div className="mt-4">
                <label className="block text-gray-700 font-semibold">Update Location:</label>
                <div className="flex">
                    <input
                        type="text"
                        value={location}
                        onChange={(e) => setLocation(e.target.value)}
                        className="flex-1 p-2 border rounded-l-md"
                        placeholder="Enter current location..."
                    />
                    <button
                        onClick={updateLocation}
                        className="bg-blue-600 text-white px-4 py-2 rounded-r-md hover:bg-blue-700"
                    >
                        Update
                    </button>
                </div>
            </div>

            {/* Chat Section */}
            <div className="mt-6 border rounded-lg p-4 bg-gray-100">
                <h2 className="text-lg font-semibold">Chat</h2>
                <div className="h-48 overflow-y-auto border p-2 bg-white rounded">
                    {messages.length === 0 ? (
                        <p className="text-gray-500 text-center py-4">No messages yet</p>
                    ) : (
                        messages.map((msg, index) => (
                            <div key={index} className={`mb-2 ${msg.sender === "delivery" ? 'text-right' : 'text-left'}`}>
                                <p className={`inline-block p-2 rounded-lg ${msg.sender === "delivery" ? 'bg-blue-100 text-blue-800' : 'bg-gray-200 text-gray-800'}`}>
                                    <strong>{msg.sender === "delivery" ? "You: " : "Customer: "}</strong>
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

export default DeliveryTrackingPage;
