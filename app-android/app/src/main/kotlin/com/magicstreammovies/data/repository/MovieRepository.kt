package com.magicstreammovies.data.repository

import com.magicstreammovies.data.api.ApiClient
import com.magicstreammovies.data.model.Genre
import com.magicstreammovies.data.model.Movie

class MovieRepository : BaseRepository() {
    fun movies(callback: ResultCallback<List<Movie>>) =
        execute(ApiClient.service.movies(), callback)

    fun movie(id: String, callback: ResultCallback<Movie>) =
        execute(ApiClient.service.movie(id), callback)

    fun search(query: String, callback: ResultCallback<List<Movie>>) =
        execute(ApiClient.service.search(query), callback)

    fun genres(callback: ResultCallback<List<Genre>>) =
        execute(ApiClient.service.genres(), callback)

    fun moviesByGenre(name: String, callback: ResultCallback<List<Movie>>) =
        execute(ApiClient.service.moviesByGenre(name), callback)
}
