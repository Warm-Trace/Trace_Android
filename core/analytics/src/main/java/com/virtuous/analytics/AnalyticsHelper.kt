package com.virtuous.analytics

import com.virtuous.analytics.AnalyticsEvent.PropertiesKeys.ACTION_NAME
import com.virtuous.analytics.AnalyticsEvent.PropertiesKeys.SCREEN_NAME
import com.virtuous.analytics.AnalyticsEvent.Types.ACTION

abstract class AnalyticsHelper {
    abstract fun logEvent(event: AnalyticsEvent)
    abstract fun setUserId(id: String)
    abstract fun clearUserId()

    fun trackActionEvent(
        screenName: String,
        actionName: String,
        properties: MutableMap<String, Any?>? = null,
    ) {
        val eventProperties = mutableMapOf<String, Any?>(
            SCREEN_NAME to screenName,
            ACTION_NAME to actionName,
        )

        properties?.let { eventProperties.putAll(it) }

        logEvent(
            AnalyticsEvent(
                type = ACTION,
                properties = eventProperties,
            ),
        )
    }
}
