package com.example.app.repository

import android.util.Log
import com.here.sdk.core.GeoCircle
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.core.LanguageCode
import com.here.sdk.search.Place
import com.here.sdk.search.SearchCallback
import com.here.sdk.search.SearchEngine
import com.here.sdk.search.SearchError
import com.here.sdk.search.SearchOptions
import com.here.sdk.search.TextQuery

class SearchRepository(private val searchEngine: SearchEngine) {

    fun searchLocation(query: String, userCoordinates: GeoCoordinates?, callback: (List<Place>?) -> Unit) {
        if (userCoordinates == null) {
            Log.e("SearchRepository", "userCoordinates is null")
            callback(null)
            return
        }

        val searchOptions = SearchOptions().apply {
            maxItems = 30
            languageCode = LanguageCode.VI_VN
        }

        val searchArea = GeoCircle(userCoordinates, 10000.0)
        val addressQuery = TextQuery(query, TextQuery.Area(searchArea))

        searchEngine.search(addressQuery, searchOptions, object : SearchCallback {
            override fun onSearchCompleted(searchError: SearchError?, places: MutableList<Place>?) {
                if (searchError != null) {
                    Log.e("Search", "Tìm kiếm thất bại: ${searchError.name}")
                    callback(null)
                } else {
                    callback(places)
                }
            }
        })
    }
}