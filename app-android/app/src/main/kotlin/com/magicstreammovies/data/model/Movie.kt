package com.magicstreammovies.data.model

import com.google.gson.annotations.SerializedName

data class Movie(
    val id: String? = null,
    val title: String? = null,
    @SerializedName("poster_path") val posterPath: String? = null,
    @SerializedName("youtube_id") val youtubeId: String? = null,
    @SerializedName("admin_review") val adminReview: String? = null,
    val genre: List<Genre>? = null,
    val ranking: Ranking? = null
) {
    fun genreText() = genre.orEmpty().mapNotNull { it.name }.joinToString().ifEmpty { "N/A" }

    fun rankingText() = ranking?.name?.takeIf { it.isNotEmpty() } ?: ranking?.value ?: "N/A"
}
