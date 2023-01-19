package com.github.search.network.model

import com.google.gson.annotations.SerializedName

data class RepositoryItem(
    @SerializedName("name") val name: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("description") val description: String,
    @SerializedName("owner") val owner: Owner
)
