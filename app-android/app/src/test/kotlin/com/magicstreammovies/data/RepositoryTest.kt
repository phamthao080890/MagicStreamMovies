package com.magicstreammovies.data

import com.google.gson.JsonParser
import com.magicstreammovies.data.api.MovieApiService
import com.magicstreammovies.data.repository.ApiResult
import com.magicstreammovies.data.repository.AuthRepository
import com.magicstreammovies.data.repository.FavouriteRepository
import com.magicstreammovies.data.repository.MovieRepository
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class   RepositoryTest {
    private lateinit var server: MockWebServer
    private lateinit var movies: MovieRepository
    private lateinit var auth: AuthRepository
    private lateinit var favourites: FavouriteRepository

    @Before fun setup() {
        server = MockWebServer().apply { start() }
        val service = Retrofit.Builder().baseUrl(server.url("/api/"))
            .addConverterFactory(GsonConverterFactory.create()).build().create(MovieApiService::class.java)
        movies = MovieRepository(service)
        auth = AuthRepository(service)
        favourites = FavouriteRepository(service)
    }

    @After fun teardown() { server.shutdown() }

    private fun respond(body: String, code: Int = 200) {
        server.enqueue(MockResponse().setResponseCode(code).setHeader("Content-Type", "application/json").setBody(body))
    }

    private fun <T> awaitResult(block: ((ApiResult<T>) -> Unit) -> Unit): ApiResult<T> {
        val latch = CountDownLatch(1)
        var result: ApiResult<T>? = null
        block { result = it; latch.countDown() }
        assertTrue("Repository callback did not complete", latch.await(5, TimeUnit.SECONDS))
        return requireNotNull(result)
    }

    @Test fun `loads movies and parses response`() {
        respond("""[{"id":"m1","title":"Test film"}]""")
        val result = awaitResult { movies.movies(it) }
        assertEquals("Test film", result.data?.single()?.title)
        assertEquals("/api/movies", server.takeRequest().path)
    }

    @Test fun `loads movie details by id`() {
        respond("""{"id":"m1","title":"Detail"}""")
        assertEquals("Detail", awaitResult { movies.movie("m1", it) }.data?.title)
        assertEquals("/api/movies/m1", server.takeRequest().path)
    }

    @Test fun `recommendations encode and forward query`() {
        respond("[]")
        assertTrue(awaitResult { movies.recommendations("hanh dong", it) }.isSuccess)
        val request = server.takeRequest()
        assertEquals("/api/recommendations", request.requestUrl?.encodedPath)
        assertEquals("hanh dong", request.requestUrl?.queryParameter("q"))
    }

    @Test fun `search forwards query`() {
        respond("[]")
        awaitResult { movies.search("Harry Potter", it) }
        val request = server.takeRequest()
        assertEquals("/api/movies/search", request.requestUrl?.encodedPath)
        assertEquals("Harry Potter", request.requestUrl?.queryParameter("q"))
    }

    @Test fun `genre filter encodes path`() {
        respond("[]")
        awaitResult { movies.moviesByGenre("Khoa Học", it) }
        assertEquals("Khoa Học", server.takeRequest().requestUrl?.pathSegments?.last())
    }

    @Test fun `genres use post and parse names`() {
        respond("""[{"genre_name":"Comedy"}]""")
        assertEquals("Comedy", awaitResult { movies.genres(it) }.data?.single()?.name)
        val request = server.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/api/genres", request.path)
    }

    @Test fun `login sends credentials and parses user`() {
        respond("""{"userId":"u1","email":"a@example.com"}""")
        assertEquals("u1", awaitResult { auth.login("a@example.com", "pw", it) }.data?.userId)
        val request = server.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/api/auth/login", request.path)
        val json = JsonParser.parseString(request.body.readUtf8()).asJsonObject
        assertEquals("a@example.com", json["email"].asString)
        assertEquals("pw", json["password"].asString)
    }

    @Test fun `registration sends name email and password`() {
        respond("""{"userId":"u2"}""")
        assertEquals("u2", awaitResult { auth.register("An", "a@example.com", "pw", it) }.data?.userId)
        val request = server.takeRequest()
        assertEquals("/api/auth/register", request.path)
        val json = JsonParser.parseString(request.body.readUtf8()).asJsonObject
        assertEquals("An", json["name"].asString)
        assertEquals("a@example.com", json["email"].asString)
        assertEquals("pw", json["password"].asString)
    }

    @Test fun `loads favourite ids`() {
        respond("""["m1","m2"]""")
        assertEquals(listOf("m1", "m2"), awaitResult { favourites.favourites("u1", it) }.data)
        assertEquals("/api/users/u1/favourites", server.takeRequest().path)
    }

    @Test fun `adds favourite with post`() {
        respond("""{"message":"added"}""")
        assertEquals("added", awaitResult { favourites.add("u1", "m1", it) }.data?.get("message"))
        val request = server.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/api/users/u1/favourites/m1", request.path)
    }

    @Test fun `removes favourite with delete`() {
        respond("""{"message":"removed"}""")
        assertEquals("removed", awaitResult { favourites.remove("u1", "m1", it) }.data?.get("message"))
        val request = server.takeRequest()
        assertEquals("DELETE", request.method)
        assertEquals("/api/users/u1/favourites/m1", request.path)
    }

    @Test fun `server error becomes error result`() {
        respond("{}", 500)
        val result = awaitResult { movies.movies(it) }
        assertFalse(result.isSuccess)
        assertEquals("Request failed (500). Please try again.", result.error)
    }

    @Test fun `empty successful body becomes error result`() {
        server.enqueue(MockResponse().setResponseCode(204))
        val result = awaitResult { movies.movie("m1", it) }
        assertFalse(result.isSuccess)
        assertEquals("Request failed (204). Please try again.", result.error)
    }

    @Test fun `network disconnect reports connection error`() {
        server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START))
        val result = awaitResult { movies.movies(it) }
        assertFalse(result.isSuccess)
        assertEquals("Cannot reach the server. Check your internet connection.", result.error)
    }
}
