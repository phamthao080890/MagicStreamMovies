package com.streaming.api.services;

import com.streaming.api.models.Movie;
import com.streaming.api.repositories.MovieRepository;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecommendationServiceTest {
    @Test
    void matchesVietnameseGenreAndRanksTitleMatchesFirst() {
        MovieRepository repository = mock(MovieRepository.class);
        Movie genreMatch = new Movie();
        genreMatch.setTitle("Another movie");
        genreMatch.setGenre(List.of(Map.of("genre_name", "Action")));
        Movie titleMatch = new Movie();
        titleMatch.setTitle("Action Hero");
        titleMatch.setGenre(List.of(Map.of("genre_name", "Action")));
        Movie other = new Movie();
        other.setTitle("Comedy");
        when(repository.findAll()).thenReturn(List.of(genreMatch, other, titleMatch));
        RecommendationService service = new RecommendationService(repository);
        assertEquals(List.of(titleMatch, genreMatch), service.recommend("hành động"));
        assertTrue(service.recommend("unrelated").isEmpty());
        assertTrue(service.recommend("   ").isEmpty());
    }
}
