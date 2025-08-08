package com.virtuous.network.model.cursor

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Cursor<T>(
    val dateTime: LocalDateTime?,
    val id: T?,
)