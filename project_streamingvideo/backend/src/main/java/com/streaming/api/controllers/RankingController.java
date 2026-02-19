package com.streaming.api.controllers;

import com.streaming.api.models.Ranking;
import com.streaming.api.repositories.RankingRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rankings")
@CrossOrigin("*")
public class RankingController {

    private final RankingRepository rankingRepository;

    public RankingController(RankingRepository repository) {
        this.rankingRepository = repository;
    }

    @PostMapping
    public List<Ranking> getAllRankings() {
        return rankingRepository.findAll();
    }
}
