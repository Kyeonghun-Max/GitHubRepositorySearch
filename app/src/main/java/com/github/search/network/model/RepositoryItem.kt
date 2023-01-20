package com.github.search.network.model

import com.github.search.viewmodel.RepositoryItemViewModel
import com.google.gson.annotations.SerializedName

data class RepositoryItem(
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("owner") val owner: Owner?
) {
    fun toViewModel(): RepositoryItemViewModel {
        return RepositoryItemViewModel(owner?.id ?: "", name ?: "", description ?: "", owner?.avatarUrl ?: "")
    }
}
