package com.streaming.api.repositories;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.streaming.api.models.Movie;
import com.streaming.api.services.GenreNames;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.mongodb.client.model.Filters.regex;

@Repository
public class MovieRepository {

    private final MongoCollection<Document> collection;

    public MovieRepository(MongoDatabase mongoDatabase) {
        this.collection = mongoDatabase.getCollection("movies");
    }

    public List<Movie> findAll() {
        List<Movie> movies = new ArrayList<>();
        for (Document doc : collection.find()) {
            movies.add(toMovie(doc));
        }
        return movies;
    }

    public Optional<Movie> findById(String id) {
        Document doc = collection.find(Filters.eq("_id", new ObjectId(id))).first();
        return Optional.ofNullable(doc).map(this::toMovie);
    }

    public List<Movie> findByTitleContainingIgnoreCase(String title) {
        List<Movie> movies = new ArrayList<>();
        Bson filter = regex("title", title, "i");
        for (Document doc : collection.find(filter)) {
            movies.add(toMovie(doc));
        }
        return movies;
    }

    public List<Movie> searchByGenreName(String genreName) {
        List<Movie> movies = new ArrayList<>();
        for (Document doc : collection.find()) {
            Movie movie = toMovie(doc);
            if (movie.getGenre() != null && movie.getGenre().stream().anyMatch(g ->
                    GenreNames.matches(genreName, java.util.Objects.toString(g.get("genre_name"), "")))) {
                movies.add(movie);
            }
        }
        return movies;
    }

    // ── mapping helpers ──────────────────────────────────────────────────────

    private Movie toMovie(Document doc) {
        Movie m = new Movie();
        m.setId(doc.getObjectId("_id").toHexString());
        m.setImdb_id(doc.getString("imdb_id"));
        m.setTitle(doc.getString("title"));
        m.setDescription(doc.getString("description"));
        m.setSource_url(doc.getString("source_url"));
        m.setPoster_path(doc.getString("poster_path"));
        m.setYoutube_id(doc.getString("youtube_id"));
        m.setImdb_video_id(doc.getString("imdb_video_id"));
        m.setAdmin_review(doc.getString("admin_review"));
        m.setGenre(doc.getList("genre", Document.class) == null ? null :
            doc.getList("genre", Document.class).stream()
               .map(d -> (Map<String, Object>) new java.util.HashMap<>(d))
               .collect(java.util.stream.Collectors.toList()));
        Object ranking = doc.get("ranking");
        if (ranking instanceof Document rd) {
            m.setRanking(new java.util.HashMap<>(rd));
        }
        return m;
    }

}
