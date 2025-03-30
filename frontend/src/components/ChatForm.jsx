import React, { useRef } from 'react';
import ChatbotIcon from './ChatbotIcon';


const ChatForm = ({chatHistory, setChatHistory, generateBotResponse}) => {
    const inputRef = useRef();  

    const handleFormSubmit = (e) => {
        e.preventDefault();
        const userMessage = inputRef.current.value.trim();
        if (!userMessage) return;

        console.log(userMessage);

        inputRef.current.value = '';

        //update chat history with the user's message
        setChatHistory((history) => [...history, {role: "user", text: userMessage}]);
        //Add a "thinking ..." message from the bot
        // setChatHistory((history) => [...history, {role: "model", text: "Thinking"}]), 1000);
         //generate bot response
         generateBotResponse([...chatHistory, {role: "user", text: `Using the details provided above, please address this query: ${userMessage}`}]);

        
    };

    return (
        <form action="#" className="chat-form" onSubmit={handleFormSubmit}>
            <input 
                type="text" 
                placeholder="Message ..." 
                className="message-input" 
                required 
                ref={inputRef}  // Attach the ref here
            />
            <button type="submit" className="material-symbols-outlined">
                arrow_upward
            </button>
        </form>
    );
};

export default ChatForm;
