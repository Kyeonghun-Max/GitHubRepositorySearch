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
class MainViewModel @Inject constructor(private val repository: GitHubRepository) : ViewModel() {

    private val _adapter = MutableLiveData(RepositoryListAdapter())
    val adapter: LiveData<RepositoryListAdapter>
        get() = _adapter

    private val _totalCount = MutableLiveData(createTotalCountTest(0))
    val totalCount: LiveData<String>
        get() = _totalCount

    val inputKeyword = MutableLiveData("")

    fun observeRepository(owner: LifecycleOwner) {
        repository.result.observe(owner) {
            _totalCount.postValue(createTotalCountTest(it.totalCount))
            adapter.value?.setItems(createVms(it.items))
        }
    }

    fun onSearchBtnClicked() {
        inputKeyword.value?.let {
            repository.request(it)
        }
    }

    private fun createTotalCountTest(totalCount: Int): String {
        return "$totalCount items"
    }

    private fun createVms(items: List<RepositoryItem>): List<RepositoryItemViewModel> {
        return items.map { it.toViewModel() }
    }
}