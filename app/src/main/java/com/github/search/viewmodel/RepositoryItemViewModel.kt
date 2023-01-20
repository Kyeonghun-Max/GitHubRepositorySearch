package com.github.search.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RepositoryItemViewModel(id: String, name: String, desc: String, url: String) : ViewModel() {
    val id = MutableLiveData(id)
    val name = MutableLiveData(name)
    val desc = MutableLiveData(desc)
    val imgUrl = MutableLiveData(url)
}