package com.magicstreammovies.data.model

import com.google.gson.annotations.SerializedName

data class Genre(val id: String? = null, @SerializedName("genre_name") val name: String? = null)
