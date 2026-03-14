import React, { useState, useEffect } from "react";
import { useSearchParams } from "react-router-dom";
import { API } from "../api/api";
import MovieCard from "../components/MovieCard";
import "../components/MovieCard.css";
import "./Search.css";

export default function Search() {
    const [searchParams] = useSearchParams();
    const [query, setQuery] = useState(searchParams.get("q") || "");
    const [movies, setMovies] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    useEffect(() => {
        const q = searchParams.get("q");
        if (q && q.trim()) {
            setQuery(q);
            performSearch(q);
        }
    }, [searchParams]);

    const performSearch = async (searchQuery) => {
        setLoading(true);
        setError("");
        try {
            const response = await API.get(`/movies/search?q=${encodeURIComponent(searchQuery)}`);
            setMovies(response.data);
        } catch (err) {
            console.error(err);
            setError("Failed to search movies. Please try again.");
            setMovies([]);
        } finally {
            setLoading(false);
        }
    };

    
    return (
        <div className="search-container">
            <h1>Kết quả tìm kiếm</h1>
            {error && <p className="search-error">{error}</p>}
            <div className="cards-grid">
                {movies.map(movie => (
                    <MovieCard key={movie.id} movie={movie} />
                ))}
            </div>
            {movies.length === 0 && !loading && query && !error && (
                <p className="no-results"><span className="no-results-highlight">KHÔNG TÌM THẤY KẾT QUẢ CHO</span> "{query}"</p>
            )}
        </div>
    );
}
