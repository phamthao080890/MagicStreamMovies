package com.streaming.api.models;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MovieTest {

    @Test
    void testMovieGettersAndSetters() {
        Movie movie = new Movie();

        movie.setId("m1");
        movie.setImdb_id("tt1234567");
        movie.setTitle("Test Movie");
        movie.setPoster_path("/path/to/poster.jpg");
        movie.setYoutube_id("youtube123");
        movie.setGenre(List.of(Map.of("id", "1", "name", "Action")));
        movie.setAdmin_review("Great movie!");
        movie.setRanking(Map.of("average", 4.5, "count", 100));

        assertEquals("m1", movie.getId());
        assertEquals("tt1234567", movie.getImdb_id());
        assertEquals("Test Movie", movie.getTitle());
        assertEquals("/path/to/poster.jpg", movie.getPoster_path());
        assertEquals("youtube123", movie.getYoutube_id());
        assertNotNull(movie.getGenre());
        assertEquals(1, movie.getGenre().size());
        assertEquals("Great movie!", movie.getAdmin_review());
        assertNotNull(movie.getRanking());
    }

    @Test
    void testMovieEqualsAndHashCode() {
        Movie movie1 = new Movie();
        movie1.setId("m1");
        movie1.setTitle("Test Movie");

        Movie movie2 = new Movie();
        movie2.setId("m1");
        movie2.setTitle("Test Movie");

        Movie movie3 = new Movie();
        movie3.setId("m2");
        movie3.setTitle("Another Movie");

        assertEquals(movie1, movie2);
        assertNotEquals(movie1, movie3);
        assertEquals(movie1.hashCode(), movie2.hashCode());
    }

    @Test
    void testMovieToString() {
        Movie movie = new Movie();
        movie.setId("m1");
        movie.setTitle("Test Movie");

        String toString = movie.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("m1"));
        assertTrue(toString.contains("Test Movie"));
    }
}

