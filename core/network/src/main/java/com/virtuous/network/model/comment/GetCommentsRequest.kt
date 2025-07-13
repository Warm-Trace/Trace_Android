package com.virtuous.network.model.comment

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class GetCommentsRequest(
    val cursorDateTime: LocalDateTime?,
    val cursorId: Int?,
    val size: Int
)
