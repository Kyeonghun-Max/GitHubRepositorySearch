package com.github.search.network.model

import com.google.gson.annotations.SerializedName

data class Owner(
    @SerializedName("avatar_url") val avatarUrl: String,
    @SerializedName("login") val login: String
)

