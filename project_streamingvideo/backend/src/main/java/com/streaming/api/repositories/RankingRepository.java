package com.streaming.api.repositories;

import com.streaming.api.models.Ranking;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RankingRepository extends MongoRepository<Ranking, String> {}
