import React, { useState } from "react";
import { IoChatbubbleEllipsesOutline, IoClose } from "react-icons/io5";

const Chatbot = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState([{ text: "Bonjour ! Comment puis-je vous aider ?", sender: "bot" }]);
  const [input, setInput] = useState("");

  const toggleChatbot = () => setIsOpen(!isOpen);

  const sendMessage = () => {
    if (input.trim() === "") return;
    const newMessages = [...messages, { text: input, sender: "user" }];
    setMessages(newMessages);
    setInput("");

    // Simuler une réponse du bot
    setTimeout(() => {
      setMessages((prevMessages) => [
        ...prevMessages,
        { text: "Désolé, je suis encore en apprentissage ! 😊", sender: "bot" },
      ]);
    }, 1000);
  };

  return (
    <div className="fixed bottom-5 right-5 z-50">
      {!isOpen && (
        <button onClick={toggleChatbot} className="p-3 bg-blue-600 text-white rounded-full shadow-lg">
          <IoChatbubbleEllipsesOutline size={28} />
        </button>
      )}

      {isOpen && (
        <div className="w-72 bg-white shadow-lg rounded-lg border border-gray-300">
          <div className="flex justify-between items-center p-3 bg-blue-600 text-white rounded-t-lg">
            <span>Chatbot</span>
            <button onClick={toggleChatbot}>
              <IoClose size={20} />
            </button>
          </div>

          <div className="h-64 overflow-y-auto p-3">
            {messages.map((msg, index) => (
              <div key={index} className={`my-1 p-2 rounded-lg ${msg.sender === "bot" ? "bg-gray-200" : "bg-blue-200 self-end text-right"}`}>
                {msg.text}
              </div>
            ))}
          </div>

          <div className="flex p-2 border-t">
            <input
              type="text"
              className="flex-1 p-2 border rounded-l-md focus:outline-none"
              value={input}
              onChange={(e) => setInput(e.target.value)}
              placeholder="Écrivez un message..."
            />
            <button className="bg-blue-600 text-white px-4 rounded-r-md" onClick={sendMessage}>
              Envoyer
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default Chatbot;
