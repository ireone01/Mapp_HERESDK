package com.example.app.helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

fun openGoogleMaps(latitude: Double, longitude: Double, context: Context) {
    val gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    mapIntent.setPackage("com.google.android.apps.maps")
    try {
        context.startActivity(mapIntent)
    } catch (e: Exception) {
        Log.e("MainActivity", "Google Maps chưa được cài đặt")
    }
}

