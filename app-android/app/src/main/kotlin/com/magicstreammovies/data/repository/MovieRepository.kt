package com.magicstreammovies.data.repository

import com.magicstreammovies.data.api.ApiClient
import com.magicstreammovies.data.api.MovieApiService
import com.magicstreammovies.data.model.Genre
import com.magicstreammovies.data.model.Movie

class MovieRepository(private val service: MovieApiService = ApiClient.service) : BaseRepository() {
    fun movies(callback: ResultCallback<List<Movie>>) =
        execute(service.movies(), callback)

    fun movie(id: String, callback: ResultCallback<Movie>) =
        execute(service.movie(id), callback)

    fun recommendations(query: String, callback: ResultCallback<List<Movie>>) =
        execute(service.recommendations(query), callback)

    fun search(query: String, callback: ResultCallback<List<Movie>>) =
        execute(service.search(query), callback)

    fun genres(callback: ResultCallback<List<Genre>>) =
        execute(service.genres(), callback)

    fun moviesByGenre(name: String, callback: ResultCallback<List<Movie>>) =
        execute(service.moviesByGenre(name), callback)
}
