package com.virtuous.app

import android.app.Application
import android.app.NotificationManager
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
        KakaoSdk.init(this, com.virtuous.trace.BuildConfig.KAKAO_NATIVE_APP_KEY)
    }

    private fun initNotification() {
        val channel = android.app.NotificationChannel(
            BACKGROUND_CHANNEL_ID,
            BACKGROUND_CHANNEL,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = BACKGROUND_CHANNEL_DESCRIPTION
        }

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}

