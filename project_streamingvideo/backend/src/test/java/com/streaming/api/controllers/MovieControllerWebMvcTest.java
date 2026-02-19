package com.streaming.api.controllers;

import com.streaming.api.models.Movie;
import com.streaming.api.repositories.MovieRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
class MovieControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieRepository movieRepository;

    @Test
    void getMovies_withoutQuery_callsFindAll() throws Exception {
        Movie m1 = new Movie();
        m1.setId("m1");
        m1.setTitle("A");

        when(movieRepository.findAll()).thenReturn(List.of(m1));

        mockMvc.perform(get("/api/movies"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value("m1"));

        verify(movieRepository).findAll();
        verifyNoMoreInteractions(movieRepository);
    }

    @Test
    void getMovies_withQuery_callsSearch() throws Exception {
        Movie m1 = new Movie();
        m1.setId("m1");
        m1.setTitle("Matrix");

        when(movieRepository.findByTitleContainingIgnoreCase("mat")).thenReturn(List.of(m1));

        mockMvc.perform(get("/api/movies").param("query", "mat"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Matrix"));

        verify(movieRepository).findByTitleContainingIgnoreCase("mat");
        verifyNoMoreInteractions(movieRepository);
    }

    @Test
    void getMovies_withEmptyQuery_callsFindAll() throws Exception {
        Movie m1 = new Movie();
        m1.setId("m1");
        m1.setTitle("A");

        when(movieRepository.findAll()).thenReturn(List.of(m1));

        mockMvc.perform(get("/api/movies").param("query", ""))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value("m1"));

        verify(movieRepository).findAll();
        verifyNoMoreInteractions(movieRepository);
    }

    @Test
    void getMovieById_found_returnsMovie() throws Exception {
        Movie m1 = new Movie();
        m1.setId("m1");
        m1.setTitle("Matrix");

        when(movieRepository.findById("m1")).thenReturn(Optional.of(m1));

        mockMvc.perform(get("/api/movies/m1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("m1"))
            .andExpect(jsonPath("$.title").value("Matrix"));

        verify(movieRepository).findById("m1");
        verifyNoMoreInteractions(movieRepository);
    }

    @Test
    void getMovieById_missing_returnsNullBody() throws Exception {
        when(movieRepository.findById("missing")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/movies/missing"))
            .andExpect(status().isOk())
            .andExpect(content().string(""));

        verify(movieRepository).findById("missing");
        verifyNoMoreInteractions(movieRepository);
    }

    @Test
    void searchMovies_callsSearchRepo() throws Exception {
        Movie m1 = new Movie();
        m1.setId("m1");
        m1.setTitle("Matrix");

        when(movieRepository.findByTitleContainingIgnoreCase("Matrix")).thenReturn(List.of(m1));

        mockMvc.perform(get("/api/movies/search").param("q", "Matrix"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("m1"));

        verify(movieRepository).findByTitleContainingIgnoreCase("Matrix");
        verifyNoMoreInteractions(movieRepository);
    }

    @Test
    void getMoviesByGenre_callsRepo() throws Exception {
        when(movieRepository.searchByGenreName("Action")).thenReturn(List.of());

        mockMvc.perform(get("/api/movies/genre/Action"))
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));

        verify(movieRepository).searchByGenreName("Action");
        verifyNoMoreInteractions(movieRepository);
    }
}

