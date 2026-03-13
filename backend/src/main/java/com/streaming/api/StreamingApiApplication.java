package com.streaming.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;

@SpringBootApplication(exclude = MongoRepositoriesAutoConfiguration.class)
public class StreamingApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(StreamingApiApplication.class, args);
	}
}
