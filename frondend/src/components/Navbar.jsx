import React, { useEffect, useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { getUser } from "../utils/auth"; // importing the auth function

export default function Navbar() {
        const location = useLocation();
        const navigate = useNavigate();
        const [user, setUser] = useState(getUser());
        const [name, setName] = useState(
            typeof window !== "undefined" ? localStorage.getItem("name") : null
        );
        const [searchQuery, setSearchQuery] = useState("");

        // Update when route changes or when auth changes
        useEffect(() => {
            setUser(getUser());
            if (typeof window !== "undefined") {
                setName(localStorage.getItem("name"));
            }
        }, [location]);

        useEffect(() => {
            const handler = () => {
                setUser(getUser());
                if (typeof window !== "undefined") {
                    setName(localStorage.getItem("name"));
                }
            };
            window.addEventListener("app:user-changed", handler);
            return () => window.removeEventListener("app:user-changed", handler);
        }, []);

        const handleSearch = (e) => {
            e.preventDefault();
            if (searchQuery.trim()) {
                navigate(`/search?q=${encodeURIComponent(searchQuery.trim())}`);
                setSearchQuery("");
            }
        };
        return (
            <nav style={{
                display: "flex",
                justifyContent: "space-between",
                padding: "15px 30px",
                background: "#222",
                color: "white",
            }}>
               {/* Left site menu */}
                <div style={{ display: "flex", gap: "20px", fontSize: "18px", alignItems: "center" }}>
                    <Link to="/" style={{ color: "white", textDecoration: "none" }}>Trang chủ</Link>
                    <Link to="/genres" style={{ color: "white", textDecoration: "none" }}>Thể loại</Link>
                    
                    {/* Show favourites only when logged in */}
                    {user && (
                            <Link
                                to="/favourites"
                                style={{ color: "white", textDecoration: "none" }}
                            >
                                Yêu thích
                            </Link>
                            )}
                    {!user && (<Link to="/register" style={{ color: "white", textDecoration: "none" }}>Đăng ký</Link>
                    )}
                            
                </div>
                
                {/* Right side user section */}
                <div style={{ display: "flex", gap: "20px", fontSize: "18px", alignItems: "center" }}>
                        {/* Search box */}
                        <form onSubmit={handleSearch} style={{ display: "flex", alignItems: "center", gap: "10px" }}>
                            <input
                                type="text"
                                value={searchQuery}
                                onChange={(e) => setSearchQuery(e.target.value)}
                                placeholder="Tìm kiếm phim..."
                                style={{
                                    padding: "5px 10px",
                                    fontSize: "16px",
                                    border: "1px solid #ccc",
                                    borderRadius: "4px",
                                    width: "200px"
                                }}
                            />
                            <button
                                type="submit"
                                style={{
                                    padding: "5px 10px",
                                    fontSize: "16px",
                                    background: "#fff",
                                    color: "#222",
                                    border: "1px solid #ccc",
                                    borderRadius: "4px",
                                    cursor: "pointer"
                                }}
                            >
                                Tìm
                            </button>
                        </form>
                        {user ? (
                            <>
                                {/* Show Profile initials in a circular outline when logged in */}
                                <Link
                                    to="/profile"
                                    style={{
                                        color: "white",
                                        textDecoration: "none",
                                        display: "inline-flex",
                                        alignItems: "center",
                                        justifyContent: "center",
                                        width: 36,
                                        height: 36,
                                        borderRadius: "50%",
                                        border: "2px solid #fff",
                                        fontWeight: 600,
                                        textTransform: "uppercase",
                                        lineHeight: 1,
                                    }}
                                    aria-label="Profile"
                                >
                                    {(() => {
                                        const base = (name && name.trim()) || (user ? user.split("@")[0] : "");
                                        return (base || "").slice(0, 2);
                                    })()}
                                </Link>
                        </>
                                ) : (
                                // If not logged in -> show Login
                                <Link to="/login" style={{ color: "white", textDecoration: "none" }}>
                                    Đăng nhập
                                </Link>
                                )}
                </div>
            </nav>
        );      
}