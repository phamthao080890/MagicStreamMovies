package com.streaming.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StreamingApiApplicationTest {

    @Test
    void contextLoads() {
        // This test ensures the application context loads successfully
    }

    @Test
    void mainMethodRuns() {
        // Run on a random port to avoid collision with a running instance
        System.setProperty("server.port", "0");
        assertDoesNotThrow(() -> StreamingApiApplication.main(new String[]{}));
        System.clearProperty("server.port");
    }
}

