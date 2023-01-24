package com.github.search.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.search.R
import com.github.search.network.model.RepositoryItem
import com.github.search.repository.GitHubRepository
import com.github.search.ui.adapter.RepositoryListAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: GitHubRepository, val adapter: RepositoryListAdapter) : ViewModel() {
    enum class ErrorType(val stringRes: Int) {
        NO_KEYWORD(R.string.no_keyword),
        NO_ITEMS(R.string.no_items),
        SEARCH_FAILED(R.string.search_failed)
    }

    private var job: Job? = null
    private val perPage = 50
    private var nextPage = 1
    private var isLoading = false
    private val totalCount = MutableLiveData(0)

    val totalCountText = MutableLiveData(getTotalCountText(0))
    val showErrorText = MutableLiveData(true)
    val errorStringRes = MutableLiveData(ErrorType.NO_KEYWORD.stringRes)
    val inputKeyword = MutableLiveData("")
    val showProgress = MutableLiveData(false)
    val loadMoreListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
            recyclerView.adapter?.let {
                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == it.itemCount - 1) {
                    loadMore()
                }
            }
        }
    }

    fun observeData(owner: LifecycleOwner) {
        totalCount.observe(owner) {
            totalCountText.postValue(getTotalCountText(it))
        }
    }

    fun onSearchBtnClicked() {
        inputKeyword.value?.let {
            if (it.isNotEmpty()) {
                keywordSearch(it)
            } else {
                showErrorText(ErrorType.NO_KEYWORD)
            }
        } ?: run {
            showErrorText(ErrorType.NO_KEYWORD)
        }
    }

    private fun keywordSearch(keyword: String) {
        if (isLoading) return

        showProgress.postValue(true)

        job = CoroutineScope(Dispatchers.IO).launch {
            val response = repository.request(keyword, perPage, 1)

            withContext(Dispatchers.Main) {
                showProgress.postValue(false)

                val responseData = response.body()
                if (response.isSuccessful && responseData != null) {
                    totalCount.postValue(responseData.totalCount)

                    if (responseData.totalCount == 0) {
                        showErrorText(ErrorType.NO_ITEMS)
                    } else {
                        showErrorText.postValue(false)
                        val resultItems = responseData.items

                        val enableLoadMore = resultItems.size < responseData.totalCount
                        if (enableLoadMore) {
                            nextPage++
                        }

                        adapter.setItems(createVms(resultItems, enableLoadMore))
                    }
                } else {
                    showErrorText(ErrorType.SEARCH_FAILED)
                }
            }
        }
    }

    fun loadMore() {
        job = CoroutineScope(Dispatchers.IO).launch {
            inputKeyword.value?.let { keyword ->
                if (!isLoading && adapter.getRealItemCount() < (totalCount.value ?: 0)) {
                    isLoading = true

                    val response = repository.request(keyword, perPage, nextPage)

                    withContext(Dispatchers.Main) {
                        showProgress.postValue(false)

                        val responseData = response.body()
                        if (response.isSuccessful && responseData != null) {
                            totalCount.postValue(responseData.totalCount)

                            if (responseData.items.isNotEmpty()) {
                                showErrorText.postValue(false)
                                val enableLoadMore = (adapter.getRealItemCount() + responseData.items.size) < responseData.totalCount
                                adapter.addItems(createVms(responseData.items, enableLoadMore))

                                if (adapter.getRealItemCount() < responseData.totalCount) {
                                    nextPage++
                                }
                            } else {
                                adapter.removeLoadingVm()
                            }
                        } else {
                            adapter.removeLoadingVm()
                        }

                        isLoading = false
                    }
                }
            }
        }
    }

    private fun showErrorText(errorType: ErrorType) {
        showErrorText.postValue(true)
        errorStringRes.postValue(errorType.stringRes)
        totalCount.postValue(0)
    }

    private fun getTotalCountText(totalCount: Int): String {
        return "Total $totalCount items"
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