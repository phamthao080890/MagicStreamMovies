package com.streaming.api.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RankingTest {

    @Test
    void testRankingGettersAndSetters() {
        Ranking ranking = new Ranking();

        ranking.setId("r1");
        ranking.setRanking_value("5");
        ranking.setRangking_name("Excellent");

        assertEquals("r1", ranking.getId());
        assertEquals("5", ranking.getRanking_value());
        assertEquals("Excellent", ranking.getRangking_name());
    }

    @Test
    void testRankingEqualsAndHashCode() {
        Ranking ranking1 = new Ranking();
        ranking1.setId("r1");
        ranking1.setRanking_value("5");
        ranking1.setRangking_name("Excellent");

        Ranking ranking2 = new Ranking();
        ranking2.setId("r1");
        ranking2.setRanking_value("5");
        ranking2.setRangking_name("Excellent");

        Ranking ranking3 = new Ranking();
        ranking3.setId("r2");
        ranking3.setRanking_value("4");
        ranking3.setRangking_name("Good");

        assertEquals(ranking1, ranking2);
        assertNotEquals(ranking1, ranking3);
        assertEquals(ranking1.hashCode(), ranking2.hashCode());
    }

    @Test
    void testRankingToString() {
        Ranking ranking = new Ranking();
        ranking.setId("r1");
        ranking.setRangking_name("Excellent");

        String toString = ranking.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("r1"));
        assertTrue(toString.contains("Excellent"));
    }
}

