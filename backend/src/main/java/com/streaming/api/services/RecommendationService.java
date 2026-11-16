package com.streaming.api.services;

import com.streaming.api.models.Movie;
import com.streaming.api.repositories.MovieRepository;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    private final MovieRepository movies;
    private static final Map<String, String> GENRES = Map.ofEntries(
        Map.entry("hanh dong", "action"), Map.entry("hai", "comedy"),
        Map.entry("chinh kich", "drama"), Map.entry("gia tuong", "fantasy"),
        Map.entry("giat gan", "thriller"), Map.entry("khoa hoc vien tuong", "sci fi"),
        Map.entry("bi an", "mystery"), Map.entry("toi pham", "crime"),
        Map.entry("mien tay", "western"), Map.entry("vui nhon", "comedy"), Map.entry("hai huoc", "comedy"), Map.entry("thu gian", "comedy"), Map.entry("tam ly", "drama"), Map.entry("cam dong", "drama"), Map.entry("gay can", "thriller"), Map.entry("hoi hop", "thriller"), Map.entry("trinh tham", "mystery"), Map.entry("dieu tra", "mystery"), Map.entry("phep thuat", "fantasy"), Map.entry("vu tru", "sci fi"), Map.entry("tinh cam", "lang man"), Map.entry("lang man", "tinh cam"), Map.entry("phieu luu", "adventure"), Map.entry("kinh di", "horror"), Map.entry("co trang", "lich su")
    );

    public RecommendationService(MovieRepository movies) { this.movies = movies; }

    public List<Movie> recommend(String query) {
        String normalized = normalize(query);
        if (normalized.isBlank()) return List.of();
        String expanded = normalized;
        for (var entry : GENRES.entrySet()) {
            if ((" " + normalized + " ").contains(" " + entry.getKey() + " ")) expanded += " " + entry.getValue();
        }
        List<String> terms = Arrays.stream(expanded.split("\\s+"))
            .filter(term -> term.length() > 1)
            .filter(term -> !java.util.Set.of("phim", "movie", "muon", "xem", "minh", "mot", "cho", "voi", "nhung", "nguoi", "the", "and", "watch", "want", "something", "about", "this", "that", "toi", "cua", "nhu", "cac", "theo", "nhat", "can", "tim", "hay", "moi").contains(term))
            .distinct().toList();
        if (terms.isEmpty()) return List.of();
        return MovieDeduplicator.unique(movies.findAll().stream()
            .map(movie -> new ScoredMovie(movie, score(movie, terms)))
            .filter(result -> result.score() > 0)
            .sorted(Comparator.comparingInt(ScoredMovie::score).reversed()
                .thenComparing(result -> result.movie().getTitle(), Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
            .map(ScoredMovie::movie).collect(Collectors.toList())).stream().limit(24).toList();
    }

    private int score(Movie movie, List<String> terms) {
        String title = normalize(movie.getTitle());
        String description = normalize(movie.getDescription());
        String genres = movie.getGenre() == null ? "" : movie.getGenre().stream()
            .map(genre -> String.valueOf(genre.getOrDefault("genre_name", "")))
            .map(RecommendationService::normalize).collect(Collectors.joining(" "));
        int score = 0;
        for (String term : terms) {
            if (containsWord(title, term)) score += 3;
            if (term.length() >= 3 && containsWord(genres, term)) score += 3;
            if (term.length() >= 3 && containsWord(description, term)) score += 2;
        }
        return score;
    }

    private static boolean containsWord(String text, String term) {
        return Arrays.asList(text.split("\\s+")).contains(term);
    }

    private static String normalize(String value) {
        if (value == null) return "";
        return Normalizer.normalize(value.toLowerCase(Locale.ROOT).replace('đ', 'd'), Normalizer.Form.NFD)
            .replaceAll("\\p{M}+", "").replaceAll("[^\\p{L}\\p{N}]+", " ").trim();
    }

    private record ScoredMovie(Movie movie, int score) {}
}
