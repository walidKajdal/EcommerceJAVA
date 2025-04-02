import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

const Profile = () => {
    const [user, setUser] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem("jwt");

        if (!token) {
            toast.error("❌ You must be logged in!");
            navigate("/login");
            return;
        }

        fetch("http://localhost:8080/backend_war_exploded/User", {
            method: "GET",
            headers: { "Authorization": `Bearer ${token}` },
        })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    setUser({ name: data.name, email: data.email });
                } else {
                    toast.error("❌ Session expired. Please login again.");
                    localStorage.removeItem("jwt");
                    navigate("/login");
                }
            })
            .catch(() => {
                toast.error("❌ Network error");
            });
    }, [navigate]);

    const handleLogout = () => {
        localStorage.removeItem("jwt");
        toast.success("✅ Logged out successfully!");
        navigate("/login");
    };

    if (!user) return <p>Loading...</p>;

    return (
        <div className="flex flex-col items-center mt-10">
            <h1 className="text-2xl font-bold">Profile</h1>
            <p><strong>Name:</strong> {user.name}</p>
            <p><strong>Email:</strong> {user.email}</p>
            <button
                onClick={handleLogout}
                className="mt-4 bg-red-500 text-white px-4 py-2"
            >
                Logout
            </button>
        </div>
    );
};

export default Profile;
