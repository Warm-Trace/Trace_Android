package com.virtuous.analytics.error

class NoOpErrorHelper : ErrorHelper  {
    override fun logError(error: Throwable) = Unit

    override fun setUserId(userId: String) = Unit

    override fun clearUserId() = Unit
}
