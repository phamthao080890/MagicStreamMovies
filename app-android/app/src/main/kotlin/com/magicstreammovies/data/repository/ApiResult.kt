package com.magicstreammovies.data.repository

data class ApiResult<T>(val data: T? = null, val error: String? = null) {
    val isSuccess get() = error == null
}
