package com.streaming.api.repositories;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.streaming.api.models.Movie;
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
        Bson filter = regex("genre.genre_name", genreName, "i");
        for (Document doc : collection.find(filter)) {
            movies.add(toMovie(doc));
        }
        return movies;
    }

    // ── mapping helpers ──────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private Movie toMovie(Document doc) {
        Movie m = new Movie();
        m.setId(doc.getObjectId("_id").toHexString());
        m.setImdb_id(doc.getString("imdb_id"));
        m.setTitle(doc.getString("title"));
        m.setPoster_path(doc.getString("poster_path"));
        m.setYoutube_id(doc.getString("youtube_id"));
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

    private Document toDocument(Movie m) {
        Document doc = new Document();
        if (m.getId() != null) doc.append("_id", new ObjectId(m.getId()));
        doc.append("imdb_id",      m.getImdb_id())
           .append("title",        m.getTitle())
           .append("poster_path",  m.getPoster_path())
           .append("youtube_id",   m.getYoutube_id())
           .append("admin_review", m.getAdmin_review())
           .append("genre",        m.getGenre())
           .append("ranking",      m.getRanking());
        return doc;
    }
}
