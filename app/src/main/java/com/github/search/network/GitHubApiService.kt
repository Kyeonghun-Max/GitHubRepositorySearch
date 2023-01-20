package com.github.search.network

import com.github.search.network.model.RepositoryItemList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubApiService {
    @GET("/search/repositories")
    fun getRepositoryList(@Query("q") query: String, @Query("per_page") perPage: Int, @Query("page") page: Int): Call<RepositoryItemList>
}