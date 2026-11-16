package com.streaming.api.services;
import com.streaming.api.models.Movie;
import java.text.Normalizer;
import java.util.*;
public final class MovieDeduplicator {
    private MovieDeduplicator() {}
    public static List<Movie> unique(List<Movie> movies) {
        Map<String, Movie> unique = new LinkedHashMap<>();
        int anonymous = 0;
        for (Movie movie : movies) {
            String title = movie.getTitle();
            String key = title == null ? "" : Normalizer.normalize(title, Normalizer.Form.NFC).toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{N}]+", " ").trim();
            if (key.isEmpty()) key = "__missing_" + anonymous++;
            Movie existing = unique.get(key);
            if (existing == null || richness(movie) > richness(existing)) unique.put(key, movie);
        }
        return new ArrayList<>(unique.values());
    }
    private static int richness(Movie movie) {
        int score = movie.getDescription() == null || movie.getDescription().isBlank() ? 0 : 2;
        if (movie.getGenre() != null && !movie.getGenre().isEmpty()) score += 2;
        if (movie.getPoster_path() != null && !movie.getPoster_path().isBlank()) score++;
        if (movie.getYoutube_id() != null && !movie.getYoutube_id().isBlank()) score += 3;
        if (movie.getImdb_video_id() != null && !movie.getImdb_video_id().isBlank()) score += 2;
        return score;
    }
}
