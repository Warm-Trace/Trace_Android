package com.virtuous.common.event

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventHelper @Inject constructor() {
    private val _eventChannel = Channel<TraceEvent>(BUFFERED)
    val eventChannel = _eventChannel.receiveAsFlow()

    fun sendEvent(event: TraceEvent) {
        _eventChannel.trySend(event)
    }
}

sealed class TraceEvent {
    data class ShowSnackBar(
        val message : String
    ) : TraceEvent()
}