package com.streaming.api.repositories;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.streaming.api.models.Ranking;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RankingRepository {

    private final MongoCollection<Document> collection;

    public RankingRepository(MongoDatabase mongoDatabase) {
        this.collection = mongoDatabase.getCollection("rankings");
    }

    public List<Ranking> findAll() {
        List<Ranking> rankings = new ArrayList<>();
        for (Document doc : collection.find()) {
            rankings.add(toRanking(doc));
        }
        return rankings;
    }

    public Ranking save(Ranking ranking) {
        if (ranking.getId() == null) {
            Document doc = toDocument(ranking);
            collection.insertOne(doc);
            ranking.setId(doc.getObjectId("_id").toHexString());
        } else {
            collection.replaceOne(
                Filters.eq("_id", new ObjectId(ranking.getId())),
                toDocument(ranking)
            );
        }
        return ranking;
    }

    // ── mapping helpers ──────────────────────────────────────────────────────

    private Ranking toRanking(Document doc) {
        Ranking r = new Ranking();
        r.setId(doc.getObjectId("_id").toHexString());
        r.setRanking_value(doc.getString("ranking_value"));
        r.setRangking_name(doc.getString("rangking_name"));
        return r;
    }

    private Document toDocument(Ranking r) {
        Document doc = new Document();
        if (r.getId() != null) doc.append("_id", new ObjectId(r.getId()));
        doc.append("ranking_value", r.getRanking_value())
           .append("rangking_name", r.getRangking_name());
        return doc;
    }
}
