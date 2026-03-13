package com.streaming.api.controllers;

import com.streaming.api.models.User;
import com.streaming.api.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Test
    void getAllUsers_returnsList() throws Exception {
        User u1 = new User();
        u1.setId("1");
        u1.setEmail("a@example.com");
        User u2 = new User();
        u2.setId("2");
        u2.setEmail("b@example.com");

        when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value("1"))
            .andExpect(jsonPath("$[1].id").value("2"));

        verify(userRepository).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUserById_found_returnsOptionalWithUser() throws Exception {
        User u1 = new User();
        u1.setId("1");
        u1.setEmail("a@example.com");

        when(userRepository.findById("1")).thenReturn(Optional.of(u1));

        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.email").value("a@example.com"));

        verify(userRepository).findById("1");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getFavouriteMovies_userMissing_returnsEmptyList() throws Exception {
        when(userRepository.findById("nope")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/nope/favourites"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(content().json("[]"));

        verify(userRepository).findById("nope");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void addFavouriteMovie_userMissing_returnsErrorBody() throws Exception {
        when(userRepository.findById("nope")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/users/nope/favourites/m1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.error").value("User not found"));

        verify(userRepository).findById("nope");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void addFavouriteMovie_whenNotPresent_addsAndSaves() throws Exception {
        User user = new User();
        user.setId("u1");
        user.setFavourite_movies(new ArrayList<>());

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/users/u1/favourites/m1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Movie added to favourites"));

        verify(userRepository).findById("u1");
        verify(userRepository).save(user);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void addFavouriteMovie_whenAlreadyPresent_doesNotSave() throws Exception {
        User user = new User();
        user.setId("u1");
        user.setFavourite_movies(new ArrayList<>(List.of("m1")));

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/users/u1/favourites/m1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Movie already exists in favourites"));

        verify(userRepository).findById("u1");
        verify(userRepository, never()).save(any());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void removeFavouriteMovie_userMissing_returnsErrorBody() throws Exception {
        when(userRepository.findById("nope")).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/users/nope/favourites/m1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.error").value("User not found"));

        verify(userRepository).findById("nope");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void removeFavouriteMovie_moviePresent_removesAndSaves() throws Exception {
        User user = new User();
        user.setId("u1");
        user.setFavourite_movies(new ArrayList<>(List.of("m1", "m2")));

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));

        mockMvc.perform(delete("/api/users/u1/favourites/m1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Movie removed from favourites"));

        verify(userRepository).findById("u1");
        verify(userRepository).save(user);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void removeFavouriteMovie_movieMissing_returnsErrorBody() throws Exception {
        User user = new User();
        user.setId("u1");
        user.setFavourite_movies(new ArrayList<>(List.of("m2")));

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));

        mockMvc.perform(delete("/api/users/u1/favourites/m1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.error").value("Movie not found in favourites"));

        verify(userRepository).findById("u1");
        verify(userRepository, never()).save(any());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUserByEmail_returnsUser() throws Exception {
        User user = new User();
        user.setId("u1");
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(user);

        mockMvc.perform(get("/api/users/email/test@example.com"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value("u1"))
            .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userRepository).findByEmail("test@example.com");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void addFavouriteMovie_whenFavouriteMoviesIsNull_createsListAndAdds() throws Exception {
        User user = new User();
        user.setId("u1");
        user.setFavourite_movies(null);

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/users/u1/favourites/m1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Movie added to favourites"));

        verify(userRepository).findById("u1");
        verify(userRepository).save(user);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void removeFavouriteMovie_whenFavouriteMoviesIsNull_returnsError() throws Exception {
        User user = new User();
        user.setId("u1");
        user.setFavourite_movies(null);

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));

        mockMvc.perform(delete("/api/users/u1/favourites/m1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.error").value("Movie not found in favourites"));

        verify(userRepository).findById("u1");
        verify(userRepository, never()).save(any());
        verifyNoMoreInteractions(userRepository);
    }
}

