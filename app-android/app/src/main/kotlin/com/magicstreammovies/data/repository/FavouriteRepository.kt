package com.magicstreammovies.data.repository

import com.magicstreammovies.data.api.ApiClient

class FavouriteRepository : BaseRepository() {
    fun favourites(userId: String, callback: ResultCallback<List<String>>) =
        execute(ApiClient.service.favourites(userId), callback)

    fun add(userId: String, movieId: String, callback: ResultCallback<Map<String, String>>) =
        execute(ApiClient.service.addFavourite(userId, movieId), callback)

    fun remove(userId: String, movieId: String, callback: ResultCallback<Map<String, String>>) =
        execute(ApiClient.service.removeFavourite(userId, movieId), callback)
}
