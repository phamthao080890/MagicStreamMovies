package com.streaming.api.repositories;

import com.streaming.api.models.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface MovieRepository extends MongoRepository<Movie, String> {
    //Search by title
    List<Movie> findByTitleContainingIgnoreCase(String title);
    
    //Search movies where genre list contains given genre
    @Query("{ 'genre.genre_name': { $regex: ?0, $options: 'i' }}")
    List<Movie> searchByGenreName(String genreName);
}

