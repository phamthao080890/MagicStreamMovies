package com.streaming.api.services;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class GenreNamesTest {
    @Test void groupsKnownTranslationsAndKeepsUnrelatedGenresDistinct() {
        assertTrue(GenreNames.matches("Hành Động", "Action"));
        assertTrue(GenreNames.matches("Tình Cảm", "Lãng Mạn"));
        assertTrue(GenreNames.matches("Hài", "Comedy"));
        assertFalse(GenreNames.matches("Tâm Lý", "Hài"));
    }
}
