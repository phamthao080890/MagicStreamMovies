package com.streaming.api.models;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserGettersAndSetters() {
        User user = new User();

        user.setId("123");
        user.setUser_id("u123");
        user.setFirst_name("John");
        user.setLast_name("Doe");
        user.setEmail("john@example.com");
        user.setPassword("password123");
        user.setRole("user");

        LocalDateTime now = LocalDateTime.now();
        user.setCreated_at(now);
        user.setUpdated_at(now);

        user.setToken("token123");
        user.setRefresh_token("refresh123");
        user.setFavourite_genres(List.of("Action", "Comedy"));
        user.setFavourite_movies(List.of("movie1", "movie2"));

        assertEquals("123", user.getId());
        assertEquals("u123", user.getUser_id());
        assertEquals("John", user.getFirst_name());
        assertEquals("Doe", user.getLast_name());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("user", user.getRole());
        assertEquals(now, user.getCreated_at());
        assertEquals(now, user.getUpdated_at());
        assertEquals("token123", user.getToken());
        assertEquals("refresh123", user.getRefresh_token());
        assertEquals(2, user.getFavourite_genres().size());
        assertEquals(2, user.getFavourite_movies().size());
    }

    @Test
    void testUserEqualsAndHashCode() {
        User user1 = new User();
        user1.setId("123");
        user1.setEmail("john@example.com");

        User user2 = new User();
        user2.setId("123");
        user2.setEmail("john@example.com");

        User user3 = new User();
        user3.setId("456");
        user3.setEmail("jane@example.com");

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testUserToString() {
        User user = new User();
        user.setId("123");
        user.setEmail("john@example.com");

        String toString = user.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("123"));
        assertTrue(toString.contains("john@example.com"));
    }
}

