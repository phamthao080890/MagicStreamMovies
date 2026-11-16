import React,{useEffect, useState} from "react";
import {API} from "../api/api";
import MovieCard from "../components/MovieCard";
import "../components/MovieCard.css";

export default function Home() {
        const [movies, setMovies] = useState([]);
        
        useEffect(() => {
            API.get("/movies")
                .then(res => {
                    setMovies(res.data);
                })
                .catch(err => {
                    console.log(err);
                });
        }, []);
        
        return (
            <div className="catalog-page">
                <p className="catalog-page__eyebrow">KHÁM PHÁ THƯ VIỆN</p>
                <h1>Tất cả phim</h1>
                <p className="catalog-page__subtitle">Tìm câu chuyện tiếp theo dành cho bạn.</p>
                <div className="cards-grid">
                {movies.map(movie => (
                        <MovieCard key = {movie.id} movie = {movie}/>
                        ))}

                </div>
            </div>
        );
        
}