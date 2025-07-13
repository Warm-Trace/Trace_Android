package com.virtuous.network.model.post

import kotlinx.serialization.Serializable

@Serializable
data class ToggleEmotionResponse(
    val emotionType : String,
    val isAdded : Boolean,
)
