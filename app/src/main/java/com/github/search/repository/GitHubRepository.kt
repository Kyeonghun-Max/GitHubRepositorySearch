package com.github.search.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.search.network.GitHubApiService
import com.github.search.network.model.RepositoryItem
import com.github.search.network.model.RepositoryItemList
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class GitHubRepository @Inject constructor(private val retroInstance: GitHubApiService) {

    private val _itemList: MutableLiveData<List<RepositoryItem>> = MutableLiveData()
    val itemList: LiveData<List<RepositoryItem>>
        get() = _itemList

    fun request(query: String) {
        val call = retroInstance.getRepositoryList(query)
        call.enqueue(object : Callback<RepositoryItemList> {
            override fun onResponse(call: retrofit2.Call<RepositoryItemList>, response: Response<RepositoryItemList>) {
                _itemList.postValue(response.body()?.items!!)
            }

            override fun onFailure(call: retrofit2.Call<RepositoryItemList>, t: Throwable) {
                _itemList.postValue(null)
            }
        })
    }
}