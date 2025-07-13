package com.virtuous.network.model.comment

import kotlinx.serialization.Serializable

@Serializable
data class AddCommentRequest(
    val content : String,
)
