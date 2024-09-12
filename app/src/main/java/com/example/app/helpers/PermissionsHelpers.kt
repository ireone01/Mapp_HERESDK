package com.example.app.helpers

import android.util.Log
import com.example.app.utils.PermissionsRequestor
import com.here.sdk.mapview.MapView

fun handleAndroidPermissions(permissionsRequestor: PermissionsRequestor, mapView: MapView) {
    permissionsRequestor.request(object : PermissionsRequestor.ResultListener {
        override fun permissionsGranted() {
        loadMapScene(mapView)
        }

        override fun permissionsDenied() {
            Log.e("MainActivity", "Permissions denied by user.")
        }
    })
}