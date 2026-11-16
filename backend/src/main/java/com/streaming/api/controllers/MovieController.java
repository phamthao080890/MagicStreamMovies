package com.streaming.api.controllers;

import com.streaming.api.models.Movie;
import com.streaming.api.services.MovieDeduplicator;
import com.streaming.api.repositories.MovieRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin("*")
public class MovieController {

    private final MovieRepository movieRepository;

    public MovieController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    // GET ALL MOVIES + SEARCH
    @GetMapping
    public List<Movie> getMovies(@RequestParam(required = false) String query) {
        if (query != null && !query.isEmpty()) {
            return MovieDeduplicator.unique(movieRepository.findByTitleContainingIgnoreCase(query));
        }
        return MovieDeduplicator.unique(movieRepository.findAll());
    }

    // GET MOVIE BY ID
    @GetMapping("/{movieId}")
    public Movie getMovieById(@PathVariable String movieId) {
        return movieRepository.findById(movieId).orElse(null);
    }

    // SEARCH MOVIES
    @GetMapping("/search")
    public List<Movie> searchMovies(@RequestParam("q") String query) {
        return MovieDeduplicator.unique(movieRepository.findByTitleContainingIgnoreCase(query));
    }

    @GetMapping("/genre/{genreName}")
    public List<Movie> getMoviesByGenre(@PathVariable String genreName) {
        return MovieDeduplicator.unique(movieRepository.searchByGenreName(genreName));
    }
}
