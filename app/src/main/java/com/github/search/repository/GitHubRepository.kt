package com.github.search.repository

import com.github.search.network.GitHubApiService
import javax.inject.Inject

class GitHubRepository @Inject constructor(private val retroInstance: GitHubApiService) {

    suspend fun request(query: String, perPage: Int, pageNo: Int) = retroInstance.getRepositoryList(query, perPage, pageNo)
}