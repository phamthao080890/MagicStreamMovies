package com.streaming.api.services;
import com.streaming.api.models.Movie;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
class MovieDeduplicatorTest {
    @Test void keepsRicherDuplicateAndPreservesDistinctTitles() {
        Movie first = new Movie(); first.setTitle("Lan Hương Như Cố");
        Movie richer = new Movie(); richer.setTitle("LAN HƯƠNG NHƯ CỐ!"); richer.setDescription("Nội dung phim");
        Movie other = new Movie(); other.setTitle("Lan Hương Như Cố 2");
        Movie differentAccent = new Movie(); differentAccent.setTitle("Lan Huong Nhu Co");
        assertEquals(List.of(richer, other, differentAccent), MovieDeduplicator.unique(List.of(first, richer, other, differentAccent)));
    }
}
