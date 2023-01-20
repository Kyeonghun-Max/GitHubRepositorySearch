package com.github.search.network.model

import com.github.search.viewmodel.ItemViewModel
import com.google.gson.annotations.SerializedName

data class RepositoryItem(
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("owner") val owner: Owner?
) {
    fun toViewModel(): ItemViewModel {
        return ItemViewModel(owner?.login ?: "", name ?: "", description ?: "", owner?.avatarUrl ?: "")
    }
}
