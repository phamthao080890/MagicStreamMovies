package com.streaming.api.controllers;

import com.streaming.api.models.Movie;
import com.streaming.api.services.RecommendationService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin("*")
public class RecommendationController {
    private final RecommendationService recommendations;

    public RecommendationController(RecommendationService recommendations) {
        this.recommendations = recommendations;
    }

    @GetMapping
    public List<Movie> recommend(@RequestParam(defaultValue = "") String q) {
        return recommendations.recommend(q);
    }
}
