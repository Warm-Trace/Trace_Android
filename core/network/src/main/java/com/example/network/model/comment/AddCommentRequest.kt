package com.example.network.model.comment

import kotlinx.serialization.Serializable

@Serializable
data class AddCommentRequest(
    val content : String,
)
