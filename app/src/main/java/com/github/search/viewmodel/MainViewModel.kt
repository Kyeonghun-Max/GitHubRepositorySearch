package com.github.search.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.search.R
import com.github.search.network.model.RepositoryItem
import com.github.search.repository.GitHubRepository
import com.github.search.ui.adapter.RepositoryListAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: GitHubRepository, val adapter: RepositoryListAdapter) : ViewModel() {
    enum class ErrorType(val stringRes: Int) {
        NO_KEYWORD(R.string.no_keyword),
        NO_ITEMS(R.string.no_items),
        SEARCH_FAILED(R.string.search_failed)
    }

    val totalCount = MutableLiveData(getTotalCountText(0))
    val showErrorText = MutableLiveData(false)
    val errorStringRes = MutableLiveData(ErrorType.NO_ITEMS.stringRes)
    val inputKeyword = MutableLiveData("")

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    fun observeRepository(owner: LifecycleOwner) {
        repository.result.observe(owner) { response ->
            _isLoading.postValue(false)

            response?.let {
                totalCount.postValue(getTotalCountText(it.totalCount))

                if (it.totalCount == 0) {
                    showErrorText(ErrorType.NO_ITEMS)
                } else {
                    showErrorText.postValue(false)
                    adapter.setItems(createVms(it.items))
                }
            } ?: run {
                showErrorText(ErrorType.SEARCH_FAILED)
            }
        }
    }

    fun onSearchBtnClicked() {
        inputKeyword.value?.let {
            if (it.isNotEmpty()) {
                _isLoading.postValue(true)
                repository.request(it)
            } else {
                showErrorText(ErrorType.NO_KEYWORD)
            }
        } ?: run {
            showErrorText(ErrorType.NO_KEYWORD)
        }
    }

    private fun showErrorText(errorType: ErrorType) {
        showErrorText.postValue(true)
        errorStringRes.postValue(errorType.stringRes)
        totalCount.postValue(getTotalCountText(0))
    }

    private fun getTotalCountText(totalCount: Int): String {
        return "$totalCount items"
    }

    private fun createVms(items: List<RepositoryItem>): List<RepositoryItemViewModel> {
        return items.map { it.toViewModel() }
    }
}