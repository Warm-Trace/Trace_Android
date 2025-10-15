package com.virtuous.analytics

class NoOpAnalyticsHelper : AnalyticsHelper() {
    override fun logEvent(event: AnalyticsEvent) = Unit
    override fun setUserId(id: String) = Unit
    override fun clearUserId() = Unit
}
