package com.virtuous.analytics

import com.virtuous.analytics.AnalyticsEvent.PropertiesKeys.ACTION_NAME
import com.virtuous.analytics.AnalyticsEvent.PropertiesKeys.BUTTON_NAME
import com.virtuous.analytics.AnalyticsEvent.PropertiesKeys.SCREEN_NAME
import com.virtuous.analytics.AnalyticsEvent.Types.ACTION
import com.virtuous.analytics.AnalyticsEvent.Types.BUTTON_CLICK
import com.virtuous.analytics.AnalyticsEvent.Types.SCREEN_VIEW

abstract class AnalyticsHelper {
    abstract fun logEvent(event: AnalyticsEvent)
    abstract fun logError(exception: Throwable)
    abstract fun setUserId(userId: String)

    fun trackClickEvent(
        screenName: String,
        buttonName: String,
        properties: MutableMap<String, Any?>? = null,
    ) {
        val eventProperties = mutableMapOf<String, Any?>(
            SCREEN_NAME to screenName,
            BUTTON_NAME to buttonName,
        )

        properties?.let { eventProperties.putAll(it) }

        logEvent(
            AnalyticsEvent(
                type = BUTTON_CLICK,
                properties = eventProperties
            )
        )
    }

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
                properties = eventProperties
            )
        )
    }

    fun trackScreenViewEvent(
        screenName: String,
        properties: MutableMap<String, Any?>? = null,
    ) {
        val eventProperties = mutableMapOf<String, Any?>(
            SCREEN_NAME to screenName,
        )

        properties?.let { eventProperties.putAll(it) }

        logEvent(
            AnalyticsEvent(
                type = SCREEN_VIEW,
                properties = eventProperties
            )
        )
    }
}

