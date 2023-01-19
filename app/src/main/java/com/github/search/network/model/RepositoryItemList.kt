package com.github.search.network.model

import com.google.gson.annotations.SerializedName

data class RepositoryItemList(
    @SerializedName("total_count") val totalCount: Int,
    @SerializedName("items") val items: List<RepositoryItem>
)
