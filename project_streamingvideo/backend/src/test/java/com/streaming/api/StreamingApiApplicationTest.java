package com.streaming.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class StreamingApiApplicationTest {

    @Test
    void contextLoads() {
        // This test ensures the application context loads successfully
    }

    @Test
    void mainMethodRuns() {
        assertDoesNotThrow(() -> StreamingApiApplication.main(new String[]{}));
    }
}

