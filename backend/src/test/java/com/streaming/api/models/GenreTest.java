package com.streaming.api.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenreTest {

    @Test
    void testGenreGettersAndSetters() {
        Genre genre = new Genre();

        genre.setId("g1");
        genre.setGenre_id(1);
        genre.setGenre_name("Action");

        assertEquals("g1", genre.getId());
        assertEquals(1, genre.getGenre_id());
        assertEquals("Action", genre.getGenre_name());
    }

    @Test
    void testGenreEqualsAndHashCode() {
        Genre genre1 = new Genre();
        genre1.setId("g1");
        genre1.setGenre_id(1);
        genre1.setGenre_name("Action");

        Genre genre2 = new Genre();
        genre2.setId("g1");
        genre2.setGenre_id(1);
        genre2.setGenre_name("Action");

        Genre genre3 = new Genre();
        genre3.setId("g2");
        genre3.setGenre_id(2);
        genre3.setGenre_name("Comedy");

        assertEquals(genre1, genre2);
        assertNotEquals(genre1, genre3);
        assertEquals(genre1.hashCode(), genre2.hashCode());
    }

    @Test
    void testGenreToString() {
        Genre genre = new Genre();
        genre.setId("g1");
        genre.setGenre_name("Action");

        String toString = genre.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("g1"));
        assertTrue(toString.contains("Action"));
    }
}

