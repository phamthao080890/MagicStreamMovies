import React, { useEffect } from "react";
import { getUser } from "../utils/auth";
import { logoutUser } from "../utils/auth";
import { useNavigate } from "react-router-dom";
import "./Profile.css";

export default function Profile() {
        const navigate = useNavigate();
    const user = getUser();
    const name = typeof window !== "undefined" ? localStorage.getItem("name") : null;
        
        // Redirect on mount if not logged in (avoid navigating during render)
        useEffect(() => {
            if (!user) navigate("/login");
        }, [navigate, user]);
        
        const handleLogout = () => {
            logoutUser();
            navigate("/");
        };
        
        if (!user) return null;

        return (
            <div className="profile">
                <div className="profile__card">
                    <h2 className="profile__title">Hồ sơ</h2>
                    <div className="profile__info">
                        <p className="profile__row"><span className="profile__label">Tên:</span> {name || (user ? (user.split("@")[0] || user) : "")}</p>
                        <p/>
                        <p className="profile__row"><span className="profile__label">Email:</span> {user}</p>
                    </div>
                    <div className="profile__actions">
                        <button onClick={handleLogout} className="profile__btn profile__btn--logout">Đăng xuất</button>
                    </div>
                </div>
            </div>
        );   
}