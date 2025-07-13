package com.virtuous.network.model.token

import kotlinx.serialization.Serializable

@Serializable
data class CheckTokenHealthResponse(
    val isExpired : Boolean,
)
