package com.streaming.api.controllers;

import com.streaming.api.models.User;
import com.streaming.api.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Test
    void login_userNotFound_returns404() throws Exception {
        when(userRepository.findByEmail(eq("missing@example.com"))).thenReturn(null);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"missing@example.com\",\"password\":\"pw\"}"))
            .andExpect(status().isNotFound())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("User not found"));

        verify(userRepository).findByEmail("missing@example.com");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void login_incorrectPassword_returns401() throws Exception {
        User user = new User();
        user.setId("u1");
        user.setEmail("user@example.com");
        user.setRole("USER");
        user.setFirst_name("Tony");
        user.setPassword(BCrypt.hashpw("correct", BCrypt.gensalt()));

        when(userRepository.findByEmail(eq("user@example.com"))).thenReturn(user);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"user@example.com\",\"password\":\"wrong\"}"))
            .andExpect(status().isUnauthorized())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("Incorrect password"));

        verify(userRepository).findByEmail("user@example.com");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void login_success_returns200_andUserFields() throws Exception {
        User user = new User();
        user.setId("u1");
        user.setEmail("user@example.com");
        user.setRole("USER");
        user.setFirst_name("Tony");
        user.setPassword(BCrypt.hashpw("correct", BCrypt.gensalt()));

        when(userRepository.findByEmail(eq("user@example.com"))).thenReturn(user);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"user@example.com\",\"password\":\"correct\"}"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("Login successfully"))
            .andExpect(jsonPath("$.userId").value("u1"))
            .andExpect(jsonPath("$.role").value("USER"))
            .andExpect(jsonPath("$.email").value("user@example.com"))
            .andExpect(jsonPath("$.name").value("Tony"));

        verify(userRepository).findByEmail("user@example.com");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void register_existingEmail_returns400() throws Exception {
        User existing = new User();
        existing.setId("u1");
        existing.setEmail("exists@example.com");

        when(userRepository.findByEmail(eq("exists@example.com"))).thenReturn(existing);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Tony\",\"email\":\"exists@example.com\",\"password\":\"pw\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Email already registered"));

        verify(userRepository).findByEmail("exists@example.com");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void register_success_hashesPassword_savesUser_andReturns200() throws Exception {
        when(userRepository.findByEmail(eq("new@example.com"))).thenReturn(null);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            // Simulate Mongo assigning an id on save
            if (u.getId() == null) {
                u.setId("generated-id");
            }
            return u;
        });

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Tony\",\"email\":\"new@example.com\",\"password\":\"pw123\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("User registered successfully"))
            .andExpect(jsonPath("$.userId").value("generated-id"))
            .andExpect(jsonPath("$.role").value("USER"))
            .andExpect(jsonPath("$.email").value("new@example.com"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).findByEmail("new@example.com");
        verify(userRepository).save(captor.capture());
        verifyNoMoreInteractions(userRepository);

        User saved = captor.getValue();
        assertThat(saved.getEmail()).isEqualTo("new@example.com");
        assertThat(saved.getRole()).isEqualTo("USER");
        assertThat(saved.getFirst_name()).isEqualTo("Tony");
        assertThat(saved.getFavourite_movies()).isNotNull();
        assertThat(saved.getFavourite_genres()).isNotNull();
        assertThat(BCrypt.checkpw("pw123", saved.getPassword())).isTrue();
    }
}
