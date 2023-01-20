package com.github.search.repository

import com.github.search.network.GitHubApiService
import com.github.search.network.model.RepositoryItemList
import retrofit2.Callback
import javax.inject.Inject

class GitHubRepository @Inject constructor(private val retroInstance: GitHubApiService) {

    fun request(query: String, perPage: Int, pageNo: Int, callback: Callback<RepositoryItemList>) {
        val call = retroInstance.getRepositoryList(query, perPage, pageNo)
        call.enqueue(callback)
    }
}