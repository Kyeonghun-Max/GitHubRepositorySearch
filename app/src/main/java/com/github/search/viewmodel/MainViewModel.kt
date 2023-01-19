package com.github.search.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.search.repository.GitHubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: GitHubRepository) : ViewModel() {

    val inputKeyword = MutableLiveData("")

    fun onSearchBtnClicked() {
        inputKeyword.value?.let {
            repository.request(it)
        }
    }
}