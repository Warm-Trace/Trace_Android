package com.virtuous.app

import android.app.Application
import android.app.NotificationManager
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.common.KakaoSdk
import com.virtuous.app.notification.NotificationService.Companion.BACKGROUND_CHANNEL
import com.virtuous.app.notification.NotificationService.Companion.BACKGROUND_CHANNEL_DESCRIPTION
import com.virtuous.app.notification.NotificationService.Companion.BACKGROUND_CHANNEL_ID
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TraceApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initNotification()
        initFcm()
        KakaoSdk.init(this, com.virtuous.trace.BuildConfig.KAKAO_NATIVE_APP_KEY)
    }

    private fun initFcm() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TraceApplication", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("FCM_TOKEN", token)
        }
    }

    private fun initNotification() {
        val channel = android.app.NotificationChannel(
            BACKGROUND_CHANNEL_ID,
            BACKGROUND_CHANNEL,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = BACKGROUND_CHANNEL_DESCRIPTION
        }

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}

