package com.virtuous.analytics.error

interface ErrorHelper {
    fun logError(error: Throwable)
    fun setUserId(userId: String)
    fun clearUserId()
}

