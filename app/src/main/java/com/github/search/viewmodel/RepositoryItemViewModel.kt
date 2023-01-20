package com.github.search.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RepositoryItemViewModel(login: String, name: String, desc: String, url: String) : ViewModel() {
    val login = MutableLiveData(login)
    val name = MutableLiveData(name)
    val desc = MutableLiveData(desc)
    val imgUrl = MutableLiveData(url)
}