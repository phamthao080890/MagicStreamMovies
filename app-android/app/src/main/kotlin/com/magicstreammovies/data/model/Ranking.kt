package com.magicstreammovies.data.model

import com.google.gson.annotations.SerializedName

data class Ranking(
    @SerializedName(
        value = "ranking_name",
        alternate = ["rangking_name"]
    ) val name: String? = null, @SerializedName("ranking_value") val value: String? = null
)
