import React from "react";
import { Link, useLocation } from "react-router-dom";
import { getUser } from "../utils/auth";
import "./BottomNav.css";

export default function BottomNav() {
  const user = getUser();
  const location = useLocation();
  if (!user) return null; // Only show when authenticated

  const isFav = location.pathname.startsWith("/favourites");

  return (
    <nav className="bottom-nav" aria-label="Secondary">
      <div className="bottom-nav__container">
        <Link
          to="/favourites"
          className="bottom-nav__link"
          aria-current={isFav ? "page" : undefined}
          title="Yêu thích"
        >
          {/* Simple icon substitute with emoji - can be replaced later */}
          <span role="img" aria-label="heart">❤️</span>
          <span>Yêu thích</span>
        </Link>
      </div>
    </nav>
  );
}
