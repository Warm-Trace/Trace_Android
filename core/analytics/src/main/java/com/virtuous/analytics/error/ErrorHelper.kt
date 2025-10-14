package com.virtuous.analytics.error

import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

interface ErrorHelper {
    fun logError(error: Throwable)
    fun setUserId(userId: String)
    fun clearUserId()
}

