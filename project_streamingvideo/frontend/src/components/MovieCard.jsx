import React from "react";
import {Link} from "react-router-dom";
import "./MovieCard.css";

export default function MovieCard({movie}) {
    return (
        <Link to={`/movie/${movie.id}`} className="movie-card__link">
            <div className="movie-card">
            <div className="movie-card__poster">
                <img
                    src={movie.poster_path}
                    alt={movie.title}
                    loading="lazy"
                />
            </div>
            <h3 className="movie-card__title">{movie.title}</h3>
            </div>
        </Link>
    );
}