import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";

import Navbar from "./components/Navbar";
import ScrollToTop from "./components/ScrollToTop";
import BottomNav from "./components/BottomNav";
import Home from "./pages/Home";
import MovieDetails from "./pages/MovieDetails";
import Genres from "./pages/Genres";
import Login from "./pages//Login";
import Profile from "./pages/Profile";
import Register from "./pages/Register";
import Favourites from "./pages/Favourites";

export default function App(){
        return (
            <BrowserRouter>
                <ScrollToTop />
                <Navbar /> 
                    <Routes>
                        <Route path="/" element={<Home />} />
                        <Route path="/movie/:id" element={<MovieDetails />} />
                        <Route path="/genres" element={<Genres />} />
                        <Route path="/login" element={<Login />} />
                        <Route path="/profile" element={<Profile />} />
                        <Route path="/register" element={<Register />} />
                        <Route path="/favourites" element={<Favourites />} />
                    </Routes>
                    <BottomNav />
                </BrowserRouter>
                );
        
}