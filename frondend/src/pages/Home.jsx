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
            <div style = {{ padding: "20px"}}>
                <div className="cards-grid">
                {movies.map(movie => (
                        <MovieCard key = {movie.id} movie = {movie}/>
                        ))}

                </div>
            </div>
        );
        
}