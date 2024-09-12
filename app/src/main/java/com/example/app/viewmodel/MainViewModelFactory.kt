package com.example.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.here.sdk.search.SearchEngine

class MainViewModelFactory(private val searchEngine: SearchEngine) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(searchEngine) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
