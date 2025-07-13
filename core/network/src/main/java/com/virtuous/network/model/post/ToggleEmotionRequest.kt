package com.virtuous.network.model.post

import kotlinx.serialization.Serializable

@Serializable
data class ToggleEmotionRequest(
    val emotionType: String,
)
