package com.virtuous.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject

class FirebaseAnalyticsHelper @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val firebaseCrashlytics: FirebaseCrashlytics
) : AnalyticsHelper() {

    override fun logEvent(event: AnalyticsEvent) {
        firebaseAnalytics.logEvent(event.type, event.toBundle())
    }

    override fun logError(exception: Throwable) {
        firebaseCrashlytics.recordException(exception)
    }

    override fun setUserId(userId: String) {
        firebaseAnalytics.setUserId(userId)
        firebaseCrashlytics.setUserId(userId)
    }

    private fun AnalyticsEvent.toBundle(): Bundle {
        val bundle = Bundle()
        properties?.forEach { (key, value) ->
            when (value) {
                is String -> bundle.putString(key, value)
                is Int -> bundle.putInt(key, value)
                is Long -> bundle.putLong(key, value)
                is Double -> bundle.putDouble(key, value)
                is Float -> bundle.putFloat(key, value)
                is Boolean -> bundle.putBoolean(key, value)
                else -> {}
            }
        }
        return bundle
    }

}