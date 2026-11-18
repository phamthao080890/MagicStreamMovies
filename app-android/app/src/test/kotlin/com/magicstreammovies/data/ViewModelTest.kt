package com.magicstreammovies.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.magicstreammovies.data.api.MovieApiService
import com.magicstreammovies.data.model.AuthResponse
import com.magicstreammovies.data.model.Movie
import com.magicstreammovies.data.model.Genre
import com.magicstreammovies.data.repository.AuthRepository
import com.magicstreammovies.data.repository.MovieRepository
import com.magicstreammovies.viewmodel.AuthViewModel
import com.magicstreammovies.viewmodel.MovieViewModel
import com.magicstreammovies.viewmodel.ViewState
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class ViewModelTest {
    @get:Rule val instantTasks = InstantTaskExecutorRule()
    private lateinit var server: MockWebServer
    private lateinit var movieViewModel: MovieViewModel
    private lateinit var authViewModel: AuthViewModel

    @Before fun setup() {
        server = MockWebServer().apply { start() }
        val service = Retrofit.Builder().baseUrl(server.url("/api/"))
            .addConverterFactory(GsonConverterFactory.create()).build().create(MovieApiService::class.java)
        movieViewModel = MovieViewModel(MovieRepository(service))
        authViewModel = AuthViewModel(AuthRepository(service))
    }

    @After fun teardown() { server.shutdown() }

    @Test fun `recommendation reports loading then data`() {
        server.enqueue(MockResponse().setBody("""[{"id":"m1","title":"Film"}]"""))
        val states = CopyOnWriteArrayList<ViewState<List<Movie>>>()
        val complete = CountDownLatch(1)
        val observer = Observer<ViewState<List<Movie>>> { states.add(it); if (!it.loading) complete.countDown() }
        movieViewModel.movies().observeForever(observer)
        try {
            movieViewModel.recommend("action")
            assertTrue(complete.await(5, TimeUnit.SECONDS))
            assertTrue(states.first().loading)
            assertEquals("Film", states.last().data?.single()?.title)
            assertNull(states.last().error)
        } finally { movieViewModel.movies().removeObserver(observer) }
    }

    @Test fun `detail failure clears loading and exposes error`() {
        server.enqueue(MockResponse().setResponseCode(404).setBody("{}"))
        val states = CopyOnWriteArrayList<ViewState<Movie>>()
        val complete = CountDownLatch(1)
        val observer = Observer<ViewState<Movie>> { states.add(it); if (!it.loading) complete.countDown() }
        movieViewModel.detail().observeForever(observer)
        try {
            movieViewModel.loadDetail("missing")
            assertTrue(complete.await(5, TimeUnit.SECONDS))
            assertTrue(states.first().loading)
            assertEquals("Request failed (404). Please try again.", states.last().error)
            assertFalse(states.last().loading)
        } finally { movieViewModel.detail().removeObserver(observer) }
    }

    @Test fun `login reports loading then account`() {
        server.enqueue(MockResponse().setBody("""{"userId":"u1"}"""))
        val states = CopyOnWriteArrayList<ViewState<AuthResponse>>()
        val complete = CountDownLatch(1)
        val observer = Observer<ViewState<AuthResponse>> { states.add(it); if (!it.loading) complete.countDown() }
        authViewModel.state().observeForever(observer)
        try {
            authViewModel.login("a@example.com", "pw")
            assertTrue(complete.await(5, TimeUnit.SECONDS))
            assertTrue(states.first().loading)
            assertEquals("u1", states.last().data?.userId)
        } finally { authViewModel.state().removeObserver(observer) }
    }

    @Test fun `registration failure exposes error`() {
        server.enqueue(MockResponse().setResponseCode(400).setBody("{}"))
        val complete = CountDownLatch(1)
        val observer = Observer<ViewState<AuthResponse>> { if (!it.loading) complete.countDown() }
        authViewModel.state().observeForever(observer)
        try {
            authViewModel.register("An", "a@example.com", "pw")
            assertTrue(complete.await(5, TimeUnit.SECONDS))
            assertEquals("Request failed (400). Please try again.", authViewModel.state().value?.error)
        } finally { authViewModel.state().removeObserver(observer) }
    }

    @Test fun `load movies emits empty results`() {
        server.enqueue(MockResponse().setBody("[]"))
        val complete = CountDownLatch(1)
        val observer = Observer<ViewState<List<Movie>>> { if (!it.loading) complete.countDown() }
        movieViewModel.movies().observeForever(observer)
        try {
            movieViewModel.loadMovies()
            assertTrue(complete.await(5, TimeUnit.SECONDS))
            assertEquals(emptyList<Movie>(), movieViewModel.movies().value?.data)
            assertEquals("/api/movies", server.takeRequest().path)
        } finally { movieViewModel.movies().removeObserver(observer) }
    }

    @Test fun `search failure reaches movie state`() {
        server.enqueue(MockResponse().setResponseCode(503))
        val complete = CountDownLatch(1)
        val observer = Observer<ViewState<List<Movie>>> { if (!it.loading) complete.countDown() }
        movieViewModel.movies().observeForever(observer)
        try {
            movieViewModel.search("film")
            assertTrue(complete.await(5, TimeUnit.SECONDS))
            assertEquals("Request failed (503). Please try again.", movieViewModel.movies().value?.error)
        } finally { movieViewModel.movies().removeObserver(observer) }
    }

    @Test fun `genre filter returns movies`() {
        server.enqueue(MockResponse().setBody("""[{"title":"Action film"}]"""))
        val complete = CountDownLatch(1)
        val observer = Observer<ViewState<List<Movie>>> { if (!it.loading) complete.countDown() }
        movieViewModel.movies().observeForever(observer)
        try {
            movieViewModel.loadByGenre("Action")
            assertTrue(complete.await(5, TimeUnit.SECONDS))
            assertEquals("Action film", movieViewModel.movies().value?.data?.single()?.title)
        } finally { movieViewModel.movies().removeObserver(observer) }
    }

    @Test fun `genres emit parsed data`() {
        server.enqueue(MockResponse().setBody("""[{"genre_name":"Comedy"}]"""))
        val complete = CountDownLatch(1)
        val observer = Observer<ViewState<List<Genre>>> { if (!it.loading) complete.countDown() }
        movieViewModel.genres().observeForever(observer)
        try {
            movieViewModel.loadGenres()
            assertTrue(complete.await(5, TimeUnit.SECONDS))
            assertEquals("Comedy", movieViewModel.genres().value?.data?.single()?.name)
        } finally { movieViewModel.genres().removeObserver(observer) }
    }

    @Test fun `detail success emits movie`() {
        server.enqueue(MockResponse().setBody("""{"id":"m1","title":"Film"}"""))
        val complete = CountDownLatch(1)
        val observer = Observer<ViewState<Movie>> { if (!it.loading) complete.countDown() }
        movieViewModel.detail().observeForever(observer)
        try {
            movieViewModel.loadDetail("m1")
            assertTrue(complete.await(5, TimeUnit.SECONDS))
            assertEquals("Film", movieViewModel.detail().value?.data?.title)
        } finally { movieViewModel.detail().removeObserver(observer) }
    }

    @Test fun `registration success emits account`() {
        server.enqueue(MockResponse().setBody("""{"userId":"u2"}"""))
        val complete = CountDownLatch(1)
        val observer = Observer<ViewState<AuthResponse>> { if (!it.loading) complete.countDown() }
        authViewModel.state().observeForever(observer)
        try {
            authViewModel.register("An", "a@example.com", "pw")
            assertTrue(complete.await(5, TimeUnit.SECONDS))
            assertEquals("u2", authViewModel.state().value?.data?.userId)
        } finally { authViewModel.state().removeObserver(observer) }
    }
}
