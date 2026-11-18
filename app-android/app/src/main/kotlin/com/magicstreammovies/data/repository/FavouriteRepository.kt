package com.magicstreammovies.data.repository

import com.magicstreammovies.data.api.ApiClient
import com.magicstreammovies.data.api.MovieApiService

class FavouriteRepository(private val service: MovieApiService = ApiClient.service) : BaseRepository() {
    fun favourites(userId: String, callback: ResultCallback<List<String>>) =
        execute(service.favourites(userId), callback)

    fun add(userId: String, movieId: String, callback: ResultCallback<Map<String, String>>) =
        execute(service.addFavourite(userId, movieId), callback)

    fun remove(userId: String, movieId: String, callback: ResultCallback<Map<String, String>>) =
        execute(service.removeFavourite(userId, movieId), callback)
}
