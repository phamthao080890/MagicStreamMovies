import React, { useEffect, useState } from "react"; 
import { API } from "../api/api";
import { useParams } from "react-router-dom";
import "../components/MovieCard.css";
import "./MovieDetails.css";


export default function MovieDetails() {
        const { id } = useParams();
        const [movie, setMovie] = useState(null);
        const [error, setError] = useState(null);
        
        //Favourite message state
        const [favMessage, setFavMessage] = useState("");
        
        //Add to favourite function
        const handleAddFavourite = async () => {
            const userId = localStorage.getItem("userId");
            
            if (!userId) {
                setFavMessage("Please login first");
                return;
            }
            try {
                // Send the request to add movie to favourites
                const response = await API.post(`/users/${userId}/favourites/${id}`);
                //check if response is successful
                if (response.status === 200){
                    setFavMessage("Phim đã được thêm vào Yêu thích");
                } else {
                    setFavMessage ("Lỗi khi thêm vào Yêu thích");
                }     
            } catch (err) {
                //Log the actual error for debugging
                setFavMessage("Lỗi khi thêm vào Yêu thích");
            }
        };
        
        useEffect(() => {
            //Fetch movie data from the backend
            API.get(`/movies/${id}`)
                    .then(res => setMovie(res.data))
                    .catch(err => setError("Failed to load movie"));   
        }, [id]);
        
        if (error) return <div className="details"><div className="details__container"><h2>{error}</h2></div></div>;
        if (!movie) return <div className="details"><div className="details__container"><h2>Loading...</h2></div></div>;
        
        //Format genre nicely for display
        const getRankingText = () => {
            if (!movie.ranking) return "N/A";
            
            //If genre is an array
            if (Array.isArray(movie.ranking)) {
                return movie.ranking
                        .map(g => (typeof g === "object" ? g.ranking_name : g))
                        .join(", ");
            }
            
            //if genre is an object
            if (typeof movie.ranking === "object") {
                return movie.ranking.ranking_name || "N/A";
            }
            //if genre is already a string
            return movie.ranking;
        };

        //Format genre nicely for display
        const getGenreText = () => {
            if (!movie.genre) return "N/A";
            
            //If genre is an array
            if (Array.isArray(movie.genre)) {
                return movie.genre
                        .map(g => (typeof g === "object" ? g.genre_name : g))
                        .join(", ");
            }
            
            //if genre is an object
            if (typeof movie.genre === "object") {
                return movie.genre.genre_name || "N/A";
            }
            //if genre is already a string
            return movie.genre;
        };
        
        return(
            <div className="details">
                <div className="details__container">
                    <div className="details__layout">
                        <div className="details__poster">
                            <div className="movie-card__poster">
                                <img src={movie.poster_path} alt={movie.title} loading="lazy" />
                            </div>
                        </div>
                        <div className="details__content">
                            <h1 className="details__title">{movie.title}</h1>
                            <div className="details__meta">
                                <div className="details__section">
                                    <span className="details__label">Thể loại: </span>{getGenreText()}
                                </div>
                                {movie.admin_review && (
                                    <div className="details__section">
                                        <span className="details__label">Đánh giá: </span>{movie.admin_review}
                                    </div>
                                )}
                                <div className="details__section">
                                    <span className="details__label">Xếp hạng: </span>{getRankingText()}
                                </div>
                            </div>
                            <div className="details__actions">
                                <button onClick={handleAddFavourite} className="details__btn">Thêm vào Yêu thích</button>
                                {favMessage && (
                                    <p className="details__alert" role="status">{favMessage}</p>
                                )}
                            </div>
                        </div>
                    </div>

                    {movie.youtube_id && (
                        <div className="details__video" aria-label="YouTube trailer">
                            <iframe
                                src={`https://www.youtube.com/embed/${movie.youtube_id}`}
                                title="YouTube trailer"
                                allowFullScreen
                            ></iframe>
                        </div>
                    )}
                </div>
            </div>
        );
        
}