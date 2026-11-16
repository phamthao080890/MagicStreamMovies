import React, { useEffect, useState } from "react";
import { Link, useLocation } from "react-router-dom";
import { getUser } from "../utils/auth";
import "./Navbar.css";

export default function Navbar() {
    const location = useLocation();
    const [user, setUser] = useState(getUser());
    const [name, setName] = useState(() => localStorage.getItem("name"));
    useEffect(() => {
        const update = () => { setUser(getUser()); setName(localStorage.getItem("name")); };
        update();
        window.addEventListener("app:user-changed", update);
        return () => window.removeEventListener("app:user-changed", update);
    }, [location]);
    const initials = ((name && name.trim()) || (user ? user.split("@")[0] : "")).slice(0, 2);
    return (
        <header className="site-header">
            <nav className="site-nav" aria-label="Điều hướng chính">
                <Link to="/" className="site-nav__brand" aria-label="MagicStream - Nổi bật"><span className="site-nav__mark">M</span><span>MAGIC<span>STREAM</span></span></Link>
                <div className="site-nav__links">
                    <Link to="/" className={location.pathname === "/" || location.pathname === "/featured" ? "active" : undefined}>Nổi bật</Link>
                    <Link to="/all" className={location.pathname === "/all" ? "active" : undefined}>Tất cả</Link>
                    <Link to="/genres" className={location.pathname === "/genres" ? "active" : undefined}>Thể loại</Link>
                    {user && <Link to="/favourites" className={location.pathname === "/favourites" ? "active" : undefined}>Yêu thích</Link>}
                </div>
                <div className="site-nav__account">
                    {user ? <Link to="/profile" className="site-nav__avatar" aria-label="Profile">{initials}</Link> : <><Link className="site-nav__register" to="/register">Đăng ký</Link><Link className="site-nav__login" to="/login">Đăng nhập</Link></>}
                </div>
            </nav>
        </header>
    );
}
