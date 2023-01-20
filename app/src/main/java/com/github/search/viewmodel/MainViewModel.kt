package com.github.search.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.search.network.model.RepositoryItem
import com.github.search.repository.GitHubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: GitHubRepository) : ViewModel() {

    val inputKeyword = MutableLiveData("")
    val totalCount: MutableLiveData<String> = MutableLiveData(createTotalCountTest(0))
    val itemList: MutableLiveData<List<RepositoryItem>> = MutableLiveData()

    fun observeRepository(owner: LifecycleOwner) {
        repository.result.observe(owner) {
            totalCount.postValue(createTotalCountTest(it.totalCount))
            itemList.postValue(it.items)
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
}