package com.streaming.api.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestTest {

    @Test
    void testRegisterRequestFields() {
        RegisterRequest request = new RegisterRequest();

        request.first_name = "John";
        request.last_name = "Doe";
        request.email = "john@example.com";
        request.password = "password123";

        assertEquals("John", request.first_name);
        assertEquals("Doe", request.last_name);
        assertEquals("john@example.com", request.email);
        assertEquals("password123", request.password);
    }

    @Test
    void testRegisterRequestCreation() {
        RegisterRequest request = new RegisterRequest();
        assertNotNull(request);
    }
}

