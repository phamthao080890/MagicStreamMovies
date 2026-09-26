package com.magicstreammovies.data.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

open class BaseRepository {
    protected fun <T> execute(call: Call<T>, callback: ResultCallback<T>) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                callback(
                    if (response.isSuccessful && response.body() != null) ApiResult(data = response.body()) else ApiResult(
                        error = "Request failed (${response.code()}). Please try again."
                    )
                )
            }

            override fun onFailure(call: Call<T>, throwable: Throwable) {
                callback(ApiResult(error = if (throwable is IOException) "Cannot reach the server. Check your internet connection." else "Something went wrong. Please try again."))
            }
        })
    }
}
