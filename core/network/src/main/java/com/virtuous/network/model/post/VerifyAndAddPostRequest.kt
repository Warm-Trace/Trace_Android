package com.virtuous.network.model.post

import kotlinx.serialization.Serializable

@Serializable
data class VerifyAndAddPostRequest(
    val postType: String,
    val title : String,
    val content: String,
)
