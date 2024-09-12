package com.example.app.helpers

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.example.app.BuildConfig
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.core.engine.SDKNativeEngine
import com.here.sdk.core.engine.SDKOptions
import com.here.sdk.core.errors.InstantiationErrorException
import com.here.sdk.mapview.MapError
import com.here.sdk.mapview.MapMeasure
import com.here.sdk.mapview.MapScene
import com.here.sdk.mapview.MapScheme
import com.here.sdk.mapview.MapView

fun initializeHERESDK(context: Context) {
    val accessKeyID = BuildConfig.HERE_API_ACCESS_KEY_ID
    val accessKeySecret = BuildConfig.HERE_API_ACCESS_KEY_SECRET
    val options = SDKOptions(accessKeyID, accessKeySecret)
    try {

        SDKNativeEngine.makeSharedInstance(context, options)
    } catch (e: InstantiationErrorException) {
        throw RuntimeException("Initialization of HERE SDK failed: " + e.error.name)
    }
}
fun disposeHERESDK() {
    val sdkNativeEngine = SDKNativeEngine.getSharedInstance()
    sdkNativeEngine?.let {
        it.dispose()
        SDKNativeEngine.setSharedInstance(null)
    }
}

fun loadMapScene(mapView: MapView) {
    val distanceInMeters = 1000 * 10.0
    val mapMeasureZoom = MapMeasure(MapMeasure.Kind.DISTANCE, distanceInMeters)
    mapView.camera.lookAt(GeoCoordinates(21.028511, 105.804817), mapMeasureZoom)


    mapView.mapScene.loadScene(MapScheme.NORMAL_DAY, object : MapScene.LoadSceneCallback {
        override fun onLoadScene( mapError: MapError?) {
            if (mapError != null) {
                Log.d(TAG, "Loading map failed: mapError: " + mapError.name)
            } else {
                Log.d(TAG, "Map scene loaded successfully.")
            }
        }
    })
}