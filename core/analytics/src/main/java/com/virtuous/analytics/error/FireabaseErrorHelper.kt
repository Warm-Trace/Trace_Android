package com.virtuous.analytics.error

import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject

class FirebaseErrorHelper @Inject constructor(
    private val firebaseCrashlytics: FirebaseCrashlytics,
) : ErrorHelper {
    override fun logError(error: Throwable) {
        firebaseCrashlytics.recordException(error)
    }

    override fun setUserId(userId: String) = firebaseCrashlytics.setUserId(userId)
    override fun clearUserId() = firebaseCrashlytics.setUserId("")
}
