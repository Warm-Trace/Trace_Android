package com.virtuous.network.model.cursor

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Cursor(
    val dateTime : LocalDateTime?,
    val id : Int?,
)