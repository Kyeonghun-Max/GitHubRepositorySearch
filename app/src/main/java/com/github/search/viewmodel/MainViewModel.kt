package com.github.search.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.search.R
import com.github.search.network.model.RepositoryItem
import com.github.search.network.model.RepositoryItemList
import com.github.search.repository.GitHubRepository
import com.github.search.ui.adapter.RepositoryListAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: GitHubRepository, val adapter: RepositoryListAdapter) : ViewModel() {
    enum class ErrorType(val stringRes: Int) {
        NO_KEYWORD(R.string.no_keyword),
        NO_ITEMS(R.string.no_items),
        SEARCH_FAILED(R.string.search_failed)
    }

    private val perPage = 50
    private var nextPage = 1
    private val totalCount = MutableLiveData(0)
    val totalCountText = MutableLiveData(getTotalCountText(0))
    val showErrorText = MutableLiveData(false)
    val errorStringRes = MutableLiveData(ErrorType.NO_ITEMS.stringRes)
    val inputKeyword = MutableLiveData("")
    val isSearching = MutableLiveData(false)
    var isMoreLoading = false

    fun observeData(owner: LifecycleOwner) {
        totalCount.observe(owner) {
            totalCountText.postValue(getTotalCountText(it))
        }
    }

    fun onSearchBtnClicked() {
        inputKeyword.value?.let {
            if (it.isNotEmpty()) {
                isSearching.postValue(true)
                keywordSearch(it)
            } else {
                showErrorText(ErrorType.NO_KEYWORD)
            }
        } ?: run {
            showErrorText(ErrorType.NO_KEYWORD)
        }
    }

    private fun keywordSearch(keyword: String) {
        repository.request(keyword, perPage, 1, object : Callback<RepositoryItemList> {
            override fun onResponse(call: Call<RepositoryItemList>, response: Response<RepositoryItemList>) {
                isSearching.postValue(false)

                response.body()?.let { resultData ->
                    totalCount.postValue(resultData.totalCount)

                    if (resultData.totalCount == 0) {
                        showErrorText(ErrorType.NO_ITEMS)
                    } else {
                        showErrorText.postValue(false)
                        val resultItems = resultData.items
                        val enableLoadMore = resultItems.size < resultData.totalCount
                        if (enableLoadMore) {
                            nextPage++
                        }

                        adapter.setItems(createVms(resultItems, enableLoadMore))
                    }
                }
            }

            override fun onFailure(call: Call<RepositoryItemList>, t: Throwable) {
                isSearching.postValue(false)
                showErrorText(ErrorType.SEARCH_FAILED)
            }
        })
    }

    fun loadMore() {
        inputKeyword.value?.let { it ->
            if (!isMoreLoading && adapter.getRealItemCount() < (totalCount.value ?: 0)) {
                isMoreLoading = true

                repository.request(it, perPage, nextPage, object : Callback<RepositoryItemList> {
                    override fun onResponse(call: Call<RepositoryItemList>, response: Response<RepositoryItemList>) {
                        isMoreLoading = false

                        response.body()?.let { resultData ->
                            totalCount.postValue(resultData.totalCount)

                            if (resultData.items.isNotEmpty()) {
                                showErrorText.postValue(false)
                                val enableLoadMore = (adapter.getRealItemCount() + resultData.items.size) < resultData.totalCount
                                adapter.addItems(createVms(resultData.items, enableLoadMore))

                                if (adapter.getRealItemCount() < resultData.totalCount) {
                                    nextPage++
                                }
                            } else {
                                adapter.removeLoadingVm()
                            }
                        }
                    }

                    override fun onFailure(call: Call<RepositoryItemList>, t: Throwable) {
                        isMoreLoading = false
                        adapter.removeLoadingVm()
                    }
                })
            }
        }
    }

    private fun showErrorText(errorType: ErrorType) {
        showErrorText.postValue(true)
        errorStringRes.postValue(errorType.stringRes)
        totalCount.postValue(0)
    }

    private fun getTotalCountText(totalCount: Int): String {
        return "$totalCount items"
    }

    private fun createVms(items: List<RepositoryItem>, enableLoadMore: Boolean): ArrayList<ViewModel> {
        return ArrayList<ViewModel>().apply {
            items.forEach {
                add(it.toViewModel())
            }
            if (enableLoadMore) {
                add(LoadingViewModel())
            }
        }
    }
}