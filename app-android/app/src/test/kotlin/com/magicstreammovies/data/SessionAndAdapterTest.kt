package com.magicstreammovies.data

import android.content.Context
import android.view.ContextThemeWrapper
import android.widget.FrameLayout
import android.widget.TextView
import com.magicstreammovies.R
import com.magicstreammovies.data.model.AuthResponse
import com.magicstreammovies.data.model.Genre
import com.magicstreammovies.data.model.Movie
import com.magicstreammovies.data.session.SessionManager
import com.magicstreammovies.ui.adapter.GenreAdapter
import com.magicstreammovies.ui.adapter.MovieAdapter
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class SessionAndAdapterTest {
    private lateinit var context: Context
    private lateinit var session: SessionManager

    @Before fun setup() {
        context = RuntimeEnvironment.getApplication()
        session = SessionManager(context)
        session.clear()
    }

    @Test fun `fresh session is logged out`() {
        assertFalse(session.isLoggedIn())
        assertNull(session.userId())
        assertEquals("", session.name())
        assertEquals("", session.email())
    }

    @Test fun `save persists user details across instances`() {
        session.save(AuthResponse(userId = "u1", name = "An", email = "an@example.com"), "fallback@example.com")
        val restored = SessionManager(context)
        assertTrue(restored.isLoggedIn())
        assertEquals("u1", restored.userId())
        assertEquals("An", restored.name())
        assertEquals("an@example.com", restored.email())
    }

    @Test fun `missing name and email use fallback email`() {
        session.save(AuthResponse(userId = "u2", name = " "), "an@example.com")
        assertEquals("An email local part", "an", session.name())
        assertEquals("an@example.com", session.email())
    }

    @Test fun `clear removes saved session`() {
        session.save(AuthResponse(userId = "u1", name = "An"), "an@example.com")
        session.clear()
        assertFalse(session.isLoggedIn())
        assertEquals("", session.email())
    }

    @Test fun `movie adapter replaces and clears data`() {
        val adapter = MovieAdapter { }
        adapter.submit(listOf(Movie(id = "1"), Movie(id = "2")))
        assertEquals(2, adapter.itemCount)
        adapter.submit(listOf(Movie(id = "3")))
        assertEquals(1, adapter.itemCount)
        adapter.submit(null)
        assertEquals(0, adapter.itemCount)
    }

    @Test fun `movie card shows title and sends selected movie`() {
        var selected: Movie? = null
        val movie = Movie(id = "1", title = "Film")
        val adapter = MovieAdapter { selected = it }
        adapter.submit(listOf(movie))
        val holder = adapter.onCreateViewHolder(FrameLayout(ContextThemeWrapper(context, R.style.Theme_MagicStream)), 0)
        adapter.onBindViewHolder(holder, 0)
        assertEquals("Film", holder.itemView.findViewById<TextView>(R.id.movie_title).text.toString())
        holder.itemView.performClick()
        assertEquals(movie, selected)
    }

    @Test fun `genre selection ignores case and click sends item`() {
        var selected: Genre? = null
        val genre = Genre(id = "1", name = "Action")
        val adapter = GenreAdapter { selected = it }
        adapter.submit(listOf(genre))
        adapter.select("action")
        val holder = adapter.onCreateViewHolder(FrameLayout(context), 0)
        adapter.onBindViewHolder(holder, 0)
        val label = holder.itemView.findViewById<TextView>(R.id.genre_label)
        assertEquals("Action", label.text.toString())
        assertTrue(label.isSelected)
        holder.itemView.performClick()
        assertEquals(genre, selected)
        adapter.submit(null)
        assertEquals(0, adapter.itemCount)
    }
}
