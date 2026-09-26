package com.magicstreammovies.data.api

import com.magicstreammovies.data.model.AuthResponse
import com.magicstreammovies.data.model.Genre
import com.magicstreammovies.data.model.LoginRequest
import com.magicstreammovies.data.model.Movie
import com.magicstreammovies.data.model.RegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {
    @GET("movies")
    fun movies(): Call<List<Movie>>

    @GET("movies/{id}")
    fun movie(@Path("id") id: String): Call<Movie>

    @GET("movies/search")
    fun search(@Query("q") query: String): Call<List<Movie>>

    @GET("movies/genre/{name}")
    fun moviesByGenre(@Path("name") name: String): Call<List<Movie>>

    @POST("genres")
    fun genres(): Call<List<Genre>>

    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<AuthResponse>

    @POST("auth/register")
    fun register(@Body request: RegisterRequest): Call<AuthResponse>

    @GET("users/{userId}/favourites")
    fun favourites(@Path("userId") userId: String): Call<List<String>>

    @POST("users/{userId}/favourites/{movieId}")
    fun addFavourite(
        @Path("userId") userId: String,
        @Path("movieId") movieId: String
    ): Call<Map<String, String>>

    @DELETE("users/{userId}/favourites/{movieId}")
    fun removeFavourite(
        @Path("userId") userId: String,
        @Path("movieId") movieId: String
    ): Call<Map<String, String>>
}
