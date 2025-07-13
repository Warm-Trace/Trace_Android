package com.virtuous.network.model.mission

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class GetCompletedMissionsRequest(
    val cursorDateTime : LocalDateTime?,
    val size : Int,
)
