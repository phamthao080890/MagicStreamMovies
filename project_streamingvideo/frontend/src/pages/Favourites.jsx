import React, { useEffect, useState } from "react";
import { API } from "../api/api";
import MovieCard from "../components/MovieCard";
import "../components/MovieCard.css";

export default function Favourites() {
        const [movies, setMovies] = useState([]);
        const [message, setMessage] = useState(""); //Message state for no movies or errors
        
        useEffect(() => {
            const userId = localStorage.getItem("userId"); //get logged in user ID
            if (!userId) {
                setMessage("Vui lòng đăng nhập để xem phim yêu thích");
                return;
            }
            //Step 1: Get favourite movie IDs
            API.get(`/users/${userId}/favourites`)
                    .then(res => {
                        const favIds = res.data;
                        if (!favIds || favIds.length === 0) {
                            setMessage("Bạn chưa có phim yêu thích nào");
                            return;
                        }
            //step 2: Fetch full movie details for each favourite ID
             return Promise.all(
                    favIds.map(id => API.get(`/movies/${id}`))
                    );
                    })
                    .then(movieResults => {                        
                        if (!movieResults) return;
                        setMovies(movieResults.map(r => r.data));
                    })
                            .catch(() => {
                                setMessage("Lỗi khi tải phim yêu thích");
                });   
        }, []);
        
        return (
                <div style={{ padding: "20px" }}>
                    
                    {message && <p>{message}</p>}
                    
                    
                    {movies.length > 0 && (
                        <div className="cards-grid">
                            {movies.map(movie => (
                                    <MovieCard key={movie.id} movie={movie} /> //render movie cards                    
                ))}
                
            </div>
        )}                           
    </div>
    );
}