package com.streaming.api.config;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
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
        seedCollection("users",   "seed/users.json",   true);
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

