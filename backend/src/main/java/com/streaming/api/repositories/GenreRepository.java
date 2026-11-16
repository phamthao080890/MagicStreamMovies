package com.streaming.api.repositories;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.streaming.api.models.Genre;
import com.streaming.api.services.GenreNames;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class GenreRepository {

    private final MongoCollection<Document> collection;
    private final MongoCollection<Document> movies;

    public GenreRepository(MongoDatabase mongoDatabase) {
        this.collection = mongoDatabase.getCollection("genres");
        this.movies = mongoDatabase.getCollection("movies");
    }

    public List<Genre> findAll() {
        java.util.Map<String, String> names = new java.util.TreeMap<>();
        for (String name : movies.distinct("genre.genre_name", String.class)) {
            String canonical = GenreNames.canonical(name);
            if (!canonical.isBlank()) names.putIfAbsent(canonical.toLowerCase(java.util.Locale.ROOT), canonical);
        }
        List<Genre> genres = new ArrayList<>();
        for (String name : names.values()) {
            Genre genre = new Genre();
            genre.setId(name);
            genre.setGenre_name(name);
            genres.add(genre);
        }
        return genres;
    }

    public Genre save(Genre genre) {
        if (genre.getId() == null) {
            Document doc = toDocument(genre);
            collection.insertOne(doc);
            genre.setId(doc.getObjectId("_id").toHexString());
        } else {
            collection.replaceOne(
                Filters.eq("_id", new ObjectId(genre.getId())),
                toDocument(genre)
            );
        }
        return genre;
    }

    // ── mapping helpers ──────────────────────────────────────────────────────

    private Genre toGenre(Document doc) {
        Genre g = new Genre();
        g.setId(doc.getObjectId("_id").toHexString());
        g.setGenre_id(doc.getInteger("genre_id"));
        g.setGenre_name(doc.getString("genre_name"));
        return g;
    }

    private Document toDocument(Genre g) {
        Document doc = new Document();
        if (g.getId() != null) doc.append("_id", new ObjectId(g.getId()));
        doc.append("genre_id",   g.getGenre_id())
           .append("genre_name", g.getGenre_name());
        return doc;
    }
}
