package com.streaming.api.repositories;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.streaming.api.models.User;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final MongoCollection<Document> collection;

    public UserRepository(MongoDatabase mongoDatabase) {
        this.collection = mongoDatabase.getCollection("users");
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        for (Document doc : collection.find()) {
            users.add(toUser(doc));
        }
        return users;
    }

    public Optional<User> findById(String id) {
        Document doc = collection.find(Filters.eq("_id", new ObjectId(id))).first();
        return Optional.ofNullable(doc).map(this::toUser);
    }

    public User findByEmail(String email) {
        Document doc = collection.find(Filters.eq("email", email)).first();
        return doc != null ? toUser(doc) : null;
    }

    public User save(User user) {
        if (user.getId() == null) {
            Document doc = toDocument(user);
            collection.insertOne(doc);
            user.setId(doc.getObjectId("_id").toHexString());
        } else {
            collection.replaceOne(
                Filters.eq("_id", new ObjectId(user.getId())),
                toDocument(user)
            );
        }
        return user;
    }

    // ── mapping helpers ──────────────────────────────────────────────────────

    private User toUser(Document doc) {
        User u = new User();
        u.setId(doc.getObjectId("_id").toHexString());
        u.setUser_id(doc.getString("user_id"));
        u.setFirst_name(doc.getString("first_name"));
        u.setLast_name(doc.getString("last_name"));
        u.setEmail(doc.getString("email"));
        u.setPassword(doc.getString("password"));
        u.setRole(doc.getString("role"));
        u.setToken(doc.getString("token"));
        u.setRefresh_token(doc.getString("refresh_token"));
        u.setFavourite_genres(doc.getList("favourite_genres", String.class));
        u.setFavourite_movies(doc.getList("favourite_movies", String.class));
        u.setCreated_at(toLocalDateTime(doc.get("created_at")));
        u.setUpdated_at(toLocalDateTime(doc.get("updated_at")));
        return u;
    }

    private Document toDocument(User u) {
        Document doc = new Document();
        if (u.getId() != null) doc.append("_id", new ObjectId(u.getId()));
        doc.append("user_id",          u.getUser_id())
           .append("first_name",       u.getFirst_name())
           .append("last_name",        u.getLast_name())
           .append("email",            u.getEmail())
           .append("password",         u.getPassword())
           .append("role",             u.getRole())
           .append("token",            u.getToken())
           .append("refresh_token",    u.getRefresh_token())
           .append("favourite_genres", u.getFavourite_genres())
           .append("favourite_movies", u.getFavourite_movies())
           .append("created_at",       toDate(u.getCreated_at()))
           .append("updated_at",       toDate(u.getUpdated_at()));
        return doc;
    }

    // ── date conversion helpers ───────────────────────────────────────────────

    /** Converts whatever BSON returns (java.util.Date or LocalDateTime) safely to LocalDateTime. */
    private LocalDateTime toLocalDateTime(Object value) {
        if (value == null) return null;
        if (value instanceof LocalDateTime ldt) return ldt;
        if (value instanceof Date d)
            return d.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime();
        return null;
    }

    /** Converts LocalDateTime to java.util.Date for BSON storage. */
    private Date toDate(LocalDateTime ldt) {
        if (ldt == null) return null;
        return Date.from(ldt.atZone(ZoneId.of("UTC")).toInstant());
    }
}
