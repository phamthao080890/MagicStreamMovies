import React, { useEffect, useState } from "react";
import { API } from "../api/api";
import MovieCard from "../components/MovieCard";
import "../components/MovieCard.css";
import "./Genres.css";

export default function Genres() {
    const [genres, setGenres] = useState([]);
    const [movies, setMovies] = useState([]);
    const [selectedGenre, setSelectedGenre] = useState(null);
        
        useEffect(() => {
            API.post("/genres")
                .then(res => {
                    const list = res.data || [];
                    setGenres(list);
                    if (list.length > 0) {
                        const first = list[0];
                        const name = typeof first === "object" ? first.genre_name : first;
                        setSelectedGenre(name);
                        API.get(`/movies/genre/${name}`)
                            .then(r => setMovies(r.data))
                            .catch(err => console.log(err));
                    }
                })
                .catch(err => console.log(err));
        }, []);
        
        const loadMoviesByGenre = (genreName) => {
            setSelectedGenre(genreName);
            API.get(`/movies/genre/${genreName}`)
                .then(res => setMovies(res.data))
                .catch(err => console.log(err));
        };
        
        return (
            <div style={{ padding: 20}}>
                <div className="genres__list">
                        {genres.map(g => {
                                   const name = g.genre_name;
                                   const normalize = (s) => (s || "").trim().toLowerCase();
                                   const isActive = normalize(selectedGenre) === normalize(name);
                                return (
                                        <button
                                key={g.id}
                                            onClick={() => loadMoviesByGenre(name)}
                                className={`genres__btn ${isActive ? "genres__btn--active" : ""}`}
                                            aria-pressed={isActive}
                                        >
                                            {name}
                                        </button>
                                );
                        })}
                </div>
                
                        
                <div className="cards-grid" style={{ marginTop: 30 }}>
                        {movies.map(movie => (
                                <MovieCard key={movie.id} movie={movie}/>
                                ))}
                
                </div>
            </div>
        );
        
}        