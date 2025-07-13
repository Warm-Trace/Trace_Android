package com.virtuous.common.util

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.core.app.ActivityCompat

object PermissionUtil {
    fun requestNotificationPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                ActivityCompat.requestPermissions(activity, arrayOf(permission), 1001)
            }

        }
    }
}