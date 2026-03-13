import React, { useState } from "react";
import { API } from "../api/api";
import { loginUser } from "../utils/auth";
import { useNavigate } from "react-router-dom";
import "./Login.css";

export default function Login() {
        const navigate = useNavigate();
        
        const [email, setEmail] = useState("");
        const [password, setPassword] = useState("");
        const [error, setError] = useState("");
        const [success, setSuccess] = useState("");
        const [loading, setLoading] = useState(false);
        
        const handleLogin = async (e) => {
            e.preventDefault();
            setError("");
            setSuccess("");
            setLoading(true);
            
            try {
                const response = await API.post("/auth/login", {
                    email,
                    password   
                });
                
                // Store auth data
                localStorage.setItem("token", response.data.token);
                localStorage.setItem("userId", response.data.userId);
                const nameFromResponse = (response && response.data && (response.data.name || (response.data.user && response.data.user.name))) || "";
                const derivedName = nameFromResponse || (email.includes("@") ? email.split("@")[0] : email);
                localStorage.setItem("name", derivedName);
                localStorage.setItem("email", email);

                // Set app-level user flag for Navbar/guards
                loginUser(email);

                setSuccess("Login successful. Redirecting to profile…");

                navigate("/");
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
            } finally {
                setLoading(false);
            }   
        };
        
        return (
            <div className="login">
                <div className="login__card">
                    <h2 className="login__title">Đăng nhập</h2>
                    <form onSubmit={handleLogin} noValidate>
                        <div className="login__field">
                            <label className="login__label" htmlFor="login-email">Email</label>
                            <input
                                id="login-email"
                                className="login__input"
                                type="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                autoComplete="email"
                                required
                            />
                        </div>
                        <div className="login__field">
                            <label className="login__label" htmlFor="login-password">Password</label>
                            <input
                                id="login-password"
                                className="login__input"
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                autoComplete="current-password"
                                required
                            />
                        </div>
                        {error && <p className="login__error" role="alert">{error}</p>}
                        {success && <p className="login__success" role="status">{success}</p>}
                        <button type="submit" className="login__submit" disabled={loading}>{loading ? "Đang đăng nhập…" : "Đăng nhập"}</button>
                    </form>
                </div>
            </div>
        );
        
}