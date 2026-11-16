package com.streaming.api.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MongoConfigTest {
    private final MongoConfig config = new MongoConfig();
    private final MongoClient client = mock(MongoClient.class);

    @Test void usesExplicitDatabaseBeforeUri() {
        MongoDatabase database = mock(MongoDatabase.class);
        when(client.getDatabase("existing")).thenReturn(database);
        assertSame(database, config.mongoDatabase(client, " existing ", "mongodb://localhost/other"));
    }

    @Test void usesDatabaseFromUriWhenNameMissing() {
        MongoDatabase database = mock(MongoDatabase.class);
        when(client.getDatabase("catalog")).thenReturn(database);
        assertSame(database, config.mongoDatabase(client, "", "mongodb://localhost/catalog"));
    }

    @Test void reportsMissingDatabaseInsteadOfChoosingAnUnexpectedDefault() {
        var error = assertThrows(IllegalStateException.class,
            () -> config.mongoDatabase(client, "", "mongodb://localhost/"));
        assertTrue(error.getMessage().contains("DATABASE_NAME"));
        verifyNoInteractions(client);
    }
}
