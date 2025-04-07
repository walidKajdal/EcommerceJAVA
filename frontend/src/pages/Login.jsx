import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const Login = () => {
    const location = useLocation();
    const navigate = useNavigate();

    useEffect(() => {
        const params = new URLSearchParams(location.search);
        if (params.get("signup") === "true") {
            setCurrentState("Sign Up");
        }
    }, [location]);

    const [currentState, setCurrentState] = useState("Login");
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        password: '',
    });

    const onChangeHandler = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const onSubmitHandler = async (e) => {
        e.preventDefault();

        const payload = {
            action: currentState === 'Login' ? 'login' : 'register',
            name: currentState === 'Sign Up' ? formData.name : undefined,
            email: formData.email,
            password: formData.password,
        };

        try {
            const response = await fetch("http://localhost:8080/User", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload),
            });

            const data = await response.json();

            if (data.success) {
                if (data.token) {
                    localStorage.setItem("jwt", data.token);
                    window.dispatchEvent(new Event("storage")); // Trigger navbar update

                    toast.success(" Account logged in successfully!", { position: "top-right" });

                    setTimeout(() => {
                        navigate("/collection");
                    }, 100);
                }
            } else {
                toast.error(` ${data.message}`);
            }
        } catch (error) {
            console.error("Login/Register error:", error);
            toast.error(" Network error");
        }
    };

    return (
        <form onSubmit={onSubmitHandler} className='flex flex-col items-center w-[90%] sm:max-w-96 m-auto mt-14 gap-4 text-gray-800'>
            <div className='inline-flex items-center gap-2 mb-2 mt-10'>
                <p className='prata-regular text-3xl'>{currentState}</p>
                <hr className='border-none h-[1.5px] w-8 bg-gray-800'/>
            </div>
            {currentState === 'Sign Up' && (
                <input
                    type="text"
                    name="name"
                    value={formData.name}
                    onChange={onChangeHandler}
                    className='w-full px-3 py-2 border border-gray-800'
                    placeholder='Name'
                    required
                />
            )}
            <input
                type="email"
                name="email"
                value={formData.email}
                onChange={onChangeHandler}
                className='w-full px-3 py-2 border border-gray-800'
                placeholder='Email'
                required
            />
            <input
                type="password"
                name="password"
                value={formData.password}
                onChange={onChangeHandler}
                className='w-full px-3 py-2 border border-gray-800'
                placeholder='Password'
                required
            />
            <div className='w-full flex justify-between text-sm mt-[-8px]'>
                <p className='cursor-pointer'>Forgot your password?</p>
                {currentState === 'Login'
                    ? <p onClick={() => setCurrentState('Sign Up')} className='cursor-pointer'>Create account</p>
                    : <p onClick={() => setCurrentState('Login')} className='cursor-pointer'>Login Here</p>}
            </div>
            <button className='bg-black text-white font-light px-8 py-2 mt-4' type="submit">
                {currentState === 'Login' ? 'Sign In' : 'Sign Up'}
            </button>
        </form>
    );
};

export default Login;

