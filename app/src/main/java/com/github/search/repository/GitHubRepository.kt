package com.github.search.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.search.network.GitHubApiService
import com.github.search.network.model.RepositoryItemList
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class GitHubRepository @Inject constructor(private val retroInstance: GitHubApiService) {

    private val _result: MutableLiveData<RepositoryItemList?> = MutableLiveData()
    val result: LiveData<RepositoryItemList?>
        get() = _result

    fun request(query: String) {
        val call = retroInstance.getRepositoryList(query)
        call.enqueue(object : Callback<RepositoryItemList> {
            override fun onResponse(call: retrofit2.Call<RepositoryItemList>, response: Response<RepositoryItemList>) {
                response.body()?.let {
                    _result.postValue(it)
                }
            }

            override fun onFailure(call: retrofit2.Call<RepositoryItemList>, t: Throwable) {
                _result.postValue(null)
            }
        })
    }
}