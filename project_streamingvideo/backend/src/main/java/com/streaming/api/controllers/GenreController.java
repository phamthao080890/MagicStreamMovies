package com.streaming.api.controllers;

import com.streaming.api.models.Genre;
import com.streaming.api.repositories.GenreRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/genres")
@CrossOrigin("*")
public class GenreController {
    private final GenreRepository genreRepository;
    
    public GenreController(GenreRepository genreRepository){
        this.genreRepository = genreRepository;
    }
    
    @PostMapping
    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }
}
