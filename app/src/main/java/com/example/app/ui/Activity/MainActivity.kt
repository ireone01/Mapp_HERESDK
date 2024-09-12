package com.example.app.ui.Activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.mapview.MapView
import com.here.sdk.search.SearchEngine
import com.example.app.utils.PermissionsRequestor
import com.example.app.R
import com.example.app.ui.Adapter.SearchResultAdapter
import com.example.app.helpers.disposeHERESDK
import com.example.app.helpers.handleAndroidPermissions
import com.example.app.helpers.initializeHERESDK
import com.example.app.helpers.openGoogleMaps
import com.example.app.viewmodel.MainViewModel
import com.example.app.viewmodel.MainViewModelFactory

class MainActivity : AppCompatActivity() {

    private val TAG ="MainActivity"
    private lateinit var permissionsRequestor: PermissionsRequestor
    private lateinit var mapView: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SearchResultAdapter


    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(SearchEngine())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeHERESDK(this)


        setContentView(R.layout.activity_main)
        searchView = findViewById(R.id.search_view)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val searchTextView = searchView.findViewById(androidx.appcompat.R.id.search_src_text) as TextView
        searchTextView.setHintTextColor(ContextCompat.getColor(this, R.color.teal_700))

        adapter = SearchResultAdapter(emptyList(), "") { place ->
            val location = place.geoCoordinates
            location?.let {
                openGoogleMaps(it.latitude, it.longitude, this)
            }
        }
        recyclerView.adapter = adapter


        viewModel.searchResults.observe(this) { places ->
            adapter.updateData(places ?: emptyList(), searchView.query.toString())
        }


        // Lấy vị trí hiện tại của người dùng
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val coordinates = GeoCoordinates(it.latitude, it.longitude)
                viewModel.updateUserCoordinates(coordinates) // Cập nhật vị trí vào ViewModel
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchLocation(it) }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.isNotEmpty()) {
                        viewModel.searchLocation(it)
                    } else {
                        adapter.updateData(emptyList(), it)
                    }
                }
                return true
            }
        })

        mapView = findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.setOnReadyListener {
            Log.d(TAG, "HERE Rendering Engine attached.")
        }

        handleAndroidPermissions(PermissionsRequestor(this), mapView)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsRequestor.onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onResume() {
        mapView.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        disposeHERESDK()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mapView.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }
}
