package com.github.search.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.search.network.model.RepositoryItem
import com.github.search.repository.GitHubRepository
import com.github.search.ui.adapter.RepositoryListAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: GitHubRepository, val adapter: RepositoryListAdapter) : ViewModel() {

    val totalCount = MutableLiveData(createTotalCountTest(0))
    val showNoResults = MutableLiveData(false)
    val inputKeyword = MutableLiveData("")

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    fun observeRepository(owner: LifecycleOwner) {
        repository.result.observe(owner) { response ->
            _isLoading.postValue(false)

            response?.let {
                totalCount.postValue(createTotalCountTest(it.totalCount))

                if (it.totalCount == 0) {
                    showNoResults.postValue(true)
                } else {
                    showNoResults.postValue(false)
                    adapter.setItems(createVms(it.items))
                }
            }
        }
    }

    fun onSearchBtnClicked() {
        inputKeyword.value?.let {
            _isLoading.postValue(true)
            repository.request(it)
        } ?: run {
            showNoResults.postValue(true)
        }
    }

    private fun createTotalCountTest(totalCount: Int): String {
        return "$totalCount items"
    }

    private fun createVms(items: List<RepositoryItem>): List<RepositoryItemViewModel> {
        return items.map { it.toViewModel() }
    }
}