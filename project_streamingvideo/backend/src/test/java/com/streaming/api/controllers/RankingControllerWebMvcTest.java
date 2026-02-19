package com.streaming.api.controllers;

import com.streaming.api.models.Ranking;
import com.streaming.api.repositories.RankingRepository;
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

@WebMvcTest(RankingController.class)
class RankingControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RankingRepository rankingRepository;

    @Test
    void postRankings_returnsAllRankings() throws Exception {
        Ranking r1 = new Ranking();
        r1.setId("r1");

        when(rankingRepository.findAll()).thenReturn(List.of(r1));

        mockMvc.perform(post("/api/rankings"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value("r1"));

        verify(rankingRepository).findAll();
        verifyNoMoreInteractions(rankingRepository);
    }

    @Test
    void getRankings_isMethodNotAllowed() throws Exception {
        mockMvc.perform(get("/api/rankings"))
            .andExpect(status().isMethodNotAllowed());

        verifyNoInteractions(rankingRepository);
    }
}

