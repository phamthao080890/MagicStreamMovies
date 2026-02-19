package com.streaming.api.controllers;

import com.streaming.api.models.Genre;
import com.streaming.api.repositories.GenreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GenreController.class)
class GenreControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenreRepository genreRepository;

    @Test
    void postGenres_returnsAllGenres() throws Exception {
        Genre g1 = new Genre();
        g1.setId("g1");

        when(genreRepository.findAll()).thenReturn(List.of(g1));

        mockMvc.perform(post("/api/genres"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value("g1"));

        verify(genreRepository).findAll();
        verifyNoMoreInteractions(genreRepository);
    }

    @Test
    void getGenres_isMethodNotAllowed() throws Exception {
        mockMvc.perform(get("/api/genres"))
            .andExpect(status().isMethodNotAllowed());

        verifyNoInteractions(genreRepository);
    }
}

