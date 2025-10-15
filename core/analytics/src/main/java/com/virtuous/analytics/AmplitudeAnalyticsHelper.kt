package com.virtuous.analytics

import com.amplitude.android.Amplitude
import com.amplitude.android.events.BaseEvent
import javax.inject.Inject
import kotlin.apply


class AmplitudeAnalyticsHelper @Inject constructor(
    private val amplitude: Amplitude,
) : AnalyticsHelper() {
    override fun logEvent(event: AnalyticsEvent) {
        amplitude.track(event.toAmplitudeEvent())
    }

    override fun setUserId(id: String) {
        amplitude.setUserId(id)
    }

    override fun clearUserId() {
        amplitude.setUserId(null)
    }

    private fun AnalyticsEvent.toAmplitudeEvent(): BaseEvent = BaseEvent().apply {
        this.eventType = type
        this.eventProperties = properties
    }
}



