package com.streaming.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;

@SpringBootApplication(exclude = {MongoRepositoriesAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class StreamingApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(StreamingApiApplication.class, args);
	}
}
