package com.example.app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.app.repository.SearchRepository
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.search.Place
import com.here.sdk.search.SearchEngine


class MainViewModel(private val searchEngine: SearchEngine) : ViewModel() {

    private val _searchResults = MutableLiveData<List<Place>>()
    val searchResults: LiveData<List<Place>> = _searchResults

    private val _userCoordinates = MutableLiveData<GeoCoordinates?>()

    //  cập nhật vị trí người dùng
    fun updateUserCoordinates(coordinates: GeoCoordinates?) {
        _userCoordinates.postValue(coordinates)
    }

    //  tìm kiếm địa điểm
    fun searchLocation(query: String) {
        val coordinates = _userCoordinates.value
        if (coordinates == null) {
            Log.e("MainViewModel", "User coordinates is null")
            return
        }

        SearchRepository(searchEngine).searchLocation(query, coordinates) { places ->
            places?.let {
                _searchResults.postValue(it)
            } ?: run {
                //  khi places là null trả về danh sách rỗng
                _searchResults.postValue(emptyList())
            }
        }

    }
}
