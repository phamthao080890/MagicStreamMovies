import React, { useState } from "react";
import { API } from "../api/api";
import { useNavigate } from "react-router-dom";
import "./Register.css";

export default function Register() {
        const navigate = useNavigate();
        
        const [name, setName] = useState("");
        const [email, setEmail] = useState("");
        const [password, setPassword] = useState("");
        const [error, setError] = useState("");
        const [success, setSuccess] = useState("");
        
        const handleRegister = async (e) => {
            e.preventDefault();
            setError("");
            setSuccess("");
            
            try {
                await API.post("/auth/register", {
                    name,
                    email,
                    password   
                });
                
                setSuccess("Account created successfully!");
                setTimeout(() => navigate("/login"), 1500);      
            } catch (err) {
                // Decide alert based on server response
                const status = err?.response?.status;
                const serverMsg = err?.response?.data?.message;
                if (serverMsg) {
                    setError(serverMsg);
                } else if (status === 400 || status === 401) {
                    setError("Invalid email or password");
                } else if (status === 403) {
                    setError("Your account is not authorized or locked.");
                } else if (status === 429) {
                    setError("Too many attempts. Please try again later.");
                } else if (status >= 500) {
                    setError("Server error. Please try again later.");
                } else {
                    setError("Network or unexpected error. Please try again.");
                }
            }   
        };
        
        return (
            <div className="register">
                <div className="register__card">
                    <h2 className="register__title">Đăng ký</h2>
                    <form onSubmit={handleRegister} noValidate>
                        <div className="register__field">
                            <label className="register__label" htmlFor="register-name">Tên</label>
                            <input
                                id="register-name"
                                className="register__input"
                                type="text"
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                                autoComplete="name"
                                required
                            />
                        </div>
                        <div className="register__field">
                            <label className="register__label" htmlFor="register-email">Email</label>
                            <input
                                id="register-email"
                                className="register__input"
                                type="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                autoComplete="email"
                                required
                            />
                        </div>
                        <div className="register__field">
                            <label className="register__label" htmlFor="register-password">Mật khẩu</label>
                            <input
                                id="register-password"
                                className="register__input"
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                autoComplete="new-password"
                                required
                            />
                        </div>
                        {error && <p className="register__error" role="alert">{error}</p>}
                        {success && <p className="register__success" role="status">{success}</p>}
                        <button type="submit" className="register__submit">Đăng ký</button>
                    </form>
                </div>
            </div>
        );       
}