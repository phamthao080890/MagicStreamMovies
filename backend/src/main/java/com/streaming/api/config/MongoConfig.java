package com.streaming.api.config;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    @Bean
    public MongoDatabase mongoDatabase(MongoClient mongoClient,
            @Value("${spring.data.mongodb.database:}") String databaseName,
            @Value("${spring.data.mongodb.uri:}") String mongoUri) {
        String name = databaseName == null ? "" : databaseName.trim();
        if (name.isEmpty() && mongoUri != null && !mongoUri.isBlank()) {
            String fromUri = new ConnectionString(mongoUri).getDatabase();
            if (fromUri != null) name = fromUri;
        }
        if (name.isEmpty()) {
            throw new IllegalStateException("MongoDB database name is missing. Set DATABASE_NAME or SPRING_DATA_MONGODB_DATABASE, or include the database name in the MongoDB URI.");
        }
        return mongoClient.getDatabase(name);
    }
}
