package com.magicstreammovies.data.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    val message: String? = null,
    @SerializedName("userId") val userId: String? = null,
    val name: String? = null,
    val email: String? = null
)
