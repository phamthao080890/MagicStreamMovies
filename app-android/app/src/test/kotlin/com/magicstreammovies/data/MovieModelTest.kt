package com.magicstreammovies.data

import com.google.gson.Gson
import com.magicstreammovies.data.model.Genre
import com.magicstreammovies.data.model.Movie
import com.magicstreammovies.data.model.Ranking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MovieModelTest {
    private val gson = Gson()

    @Test fun `movie fields deserialize from backend names`() {
        val movie = gson.fromJson("""{"id":"m1","title":"Film","poster_path":"poster","description":"Story","youtube_id":"abcdefghijk","imdb_video_id":"vi123","admin_review":"Good","genre":[{"genre_name":"Action"}],"ranking":{"ranking_name":"Great","ranking_value":"5"}}""", Movie::class.java)
        assertEquals("m1", movie.id)
        assertEquals("poster", movie.posterPath)
        assertEquals("abcdefghijk", movie.youtubeId)
        assertEquals("vi123", movie.imdbVideoId)
        assertEquals("Good", movie.adminReview)
        assertEquals("Action", movie.genreText())
        assertEquals("Great", movie.rankingText())
    }

    @Test fun `missing genre and ranking have readable defaults`() {
        val movie = gson.fromJson("{}", Movie::class.java)
        assertEquals("N/A", movie.genreText())
        assertEquals("N/A", movie.rankingText())
        assertNull(movie.youtubeId)
    }

    @Test fun `genre names omit null entries`() {
        assertEquals("Action, Comedy", Movie(genre = listOf(Genre(name = "Action"), Genre(), Genre(name = "Comedy"))).genreText())
    }

    @Test fun `ranking uses value if name is empty`() {
        assertEquals("4", Movie(ranking = Ranking(name = "", value = "4")).rankingText())
    }

    @Test fun `legacy ranking spelling is accepted`() {
        val ranking = gson.fromJson("""{"rangking_name":"Okay","ranking_value":"3"}""", Ranking::class.java)
        assertEquals("Okay", ranking.name)
        assertEquals("3", ranking.value)
    }
}
