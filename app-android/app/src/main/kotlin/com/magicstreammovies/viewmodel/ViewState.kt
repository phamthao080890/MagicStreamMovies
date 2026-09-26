package com.magicstreammovies.viewmodel

data class ViewState<T>(
    val loading: Boolean = false,
    val data: T? = null,
    val error: String? = null
)
