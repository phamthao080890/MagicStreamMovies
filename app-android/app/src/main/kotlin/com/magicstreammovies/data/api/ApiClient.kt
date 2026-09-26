package com.magicstreammovies.data.api

import com.magicstreammovies.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    val service: MovieApiService = Retrofit.Builder().baseUrl(BuildConfig.API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).build()
        .create(MovieApiService::class.java)
}
