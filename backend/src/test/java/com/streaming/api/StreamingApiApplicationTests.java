package com.streaming.api;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.streaming.api.config.DataSeeder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class StreamingApiApplicationTests {

	// Prevent real MongoClient from dialling Atlas
	@MockBean
	MongoClient mongoClient;

	// Prevent MongoConfig from needing a live connection
	@MockBean
	MongoDatabase mongoDatabase;

	// Prevent DataSeeder from running on startup (avoids NPE on mock collections)
	@MockBean
	DataSeeder dataSeeder;

	@Test
	void contextLoads() {
	}

}
