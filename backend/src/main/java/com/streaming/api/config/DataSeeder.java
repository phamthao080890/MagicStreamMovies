package com.streaming.api.config;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import java.util.Arrays;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final MongoDatabase mongoDatabase;

    public DataSeeder(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedCollection("genres",  "seed/genres.json",  false);
        seedCollection("rankings","seed/rankings.json", false);
        seedCollection("movies",  "seed/movies.json",  false);
        seedExternalMovieFiles();
        seedCollection("users",   "seed/users.json",   true);
    }

    /** Source-based seed files are additive and safe to rerun on an existing catalog. */
    private void seedExternalMovieFiles() {
        try {
            Resource[] files = new PathMatchingResourcePatternResolver().getResources("classpath*:seed/*-movies.json");
            Arrays.sort(files, java.util.Comparator.comparing(Resource::getFilename, java.util.Comparator.nullsLast(String::compareTo)));
            for (Resource file : files) seedExternalMovies(file);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to import movie seed data", e);
        }
    }

    private void seedExternalMovies(Resource file) throws Exception {
        String json;
        try (InputStream in = file.getInputStream()) { json = new String(in.readAllBytes(), StandardCharsets.UTF_8); }
        List<Document> docs = Document.parse("{\"data\":" + json + "}").getList("data", Document.class);
        if (docs == null) throw new IllegalStateException("Invalid seed file: " + file.getFilename());
        MongoCollection<Document> collection = mongoDatabase.getCollection("movies");
        int added = 0, completed = 0;
        for (Document doc : docs) {
            String sourceId = doc.getString("source_id");
            if (sourceId == null || sourceId.isBlank()) throw new IllegalStateException("Missing source_id in " + file.getFilename());
            var inserted = collection.updateOne(Filters.eq("source_id", sourceId),
                new Document("$setOnInsert", doc), new UpdateOptions().upsert(true));
            if (inserted.getUpsertedId() != null) added++;
            for (String field : List.of("title", "poster_path", "description", "source_url", "genre", "youtube_id", "imdb_video_id")) {
                Object value = doc.get(field);
                if (value == null || value instanceof String str && str.isBlank() || value instanceof List<?> list && list.isEmpty()) continue;
                if (field.equals("youtube_id") && (!(value instanceof String str) || !str.matches("[A-Za-z0-9_-]{11}"))) continue;
                if (field.equals("imdb_video_id") && (!(value instanceof String str) || !str.matches("vi[0-9]+"))) continue;
                var missing = Filters.or(Filters.exists(field, false), Filters.eq(field, null), Filters.eq(field, ""), Filters.eq(field, List.of()));
                completed += (int) collection.updateOne(Filters.and(Filters.eq("source_id", sourceId), missing),
                    new Document("$set", new Document(field, value))).getModifiedCount();
            }
        }
        log.info("[DataSeeder] '{}' processed: {} records, {} added, {} missing fields filled.", file.getFilename(), docs.size(), added, completed);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    /**
     * Imports documents from a classpath JSON file into the given collection,
     * but ONLY when the collection is currently empty.
     *
     * @param collectionName  MongoDB collection name
     * @param classpathPath   classpath-relative path to the JSON array file
     * @param hasDateFields   true when documents contain {"$date":"..."} objects
     *                        that need to be converted to LocalDateTime
     */
    private void seedCollection(String collectionName, String classpathPath, boolean hasDateFields) {
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);

        if (collection.countDocuments() > 0) {
            log.info("[DataSeeder] '{}' already has data — skipping import.", collectionName);
            return;
        }

        log.info("[DataSeeder] '{}' is empty — importing from {}...", collectionName, classpathPath);

        try {
            String json = readClasspathFile(classpathPath);

            // Parse the top-level JSON array
            List<?> rawList = Document.parse("{\"data\":" + json + "}").getList("data", Document.class);
            if (rawList == null || rawList.isEmpty()) {
                log.warn("[DataSeeder] No documents found in {}.", classpathPath);
                return;
            }

            List<Document> docs = new ArrayList<>();
            for (Object item : rawList) {
                Document doc = (Document) item;
                if (hasDateFields) {
                    convertDateFields(doc);
                }
                docs.add(doc);
            }

            collection.insertMany(docs);
            log.info("[DataSeeder] Inserted {} document(s) into '{}'.", docs.size(), collectionName);

        } catch (Exception e) {
            log.error("[DataSeeder] Failed to seed '{}': {}", collectionName, e.getMessage(), e);
        }
    }

    /** Recursively converts every {"$date": "ISO-string"} value to a LocalDateTime. */
    private void convertDateFields(Document doc) {
        for (String key : new ArrayList<>(doc.keySet())) {
            Object value = doc.get(key);

            if (value instanceof Document inner) {
                // {"$date": "2025-05-29T13:05:29.000Z"} → LocalDateTime
                if (inner.containsKey("$date")) {
                    String iso = inner.getString("$date");
                    LocalDateTime ldt = Instant.parse(iso)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDateTime();
                    doc.put(key, ldt);
                } else {
                    convertDateFields(inner);
                }
            } else if (value instanceof List<?> list) {
                for (Object element : list) {
                    if (element instanceof Document nestedDoc) {
                        convertDateFields(nestedDoc);
                    }
                }
            }
        }
    }

    /** Reads a classpath resource fully into a String. */
    private String readClasspathFile(String path) throws Exception {
        ClassPathResource resource = new ClassPathResource(path);
        try (InputStream is = resource.getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}

