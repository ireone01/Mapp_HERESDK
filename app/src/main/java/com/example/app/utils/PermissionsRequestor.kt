package com.example.app.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionsRequestor(private val activity: Activity) {

    private var resultListener: ResultListener? = null

    interface ResultListener {
        fun permissionsGranted()
        fun permissionsDenied()
    }

    fun request(resultListener: ResultListener) {
        this.resultListener = resultListener
        val missingPermissions = getPermissionsToRequest()
        if (missingPermissions.isEmpty()) {
            resultListener.permissionsGranted()
        } else {
            ActivityCompat.requestPermissions(activity, missingPermissions, PERMISSIONS_REQUEST_CODE)
        }
    }

    private fun getPermissionsToRequest(): Array<String> {
        val permissionList = ArrayList<String>()
        try {
            val packageName = activity.packageName
            val packageInfo: PackageInfo? = if (Build.VERSION.SDK_INT >= 33) {
                activity.packageManager.getPackageInfo(
                    packageName,
                    PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong())
                )
            } else {
                activity.packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_PERMISSIONS
                )
            }
            packageInfo?.requestedPermissions?.let { permissions ->
                for (permission in permissions) {
                    if (ContextCompat.checkSelfPermission(activity, permission)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M &&
                            permission == Manifest.permission.CHANGE_NETWORK_STATE
                        ) {
                            continue
                        }
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                            permission == Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ) {
                            continue
                        }
                        permissionList.add(permission)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return permissionList.toTypedArray()
    }

    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        resultListener?.let {
            if (grantResults.isEmpty()) return

            if (requestCode == PERMISSIONS_REQUEST_CODE) {
                val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
                if (allGranted) {
                    it.permissionsGranted()
                } else {
                    it.permissionsDenied()
                }
            }
        }
    }

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 42
    }
}