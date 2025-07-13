package com.virtuous.network.model.post

import kotlinx.serialization.Serializable

@Serializable
data class AddPostRequest(
    val postType: String,
    val title : String,
    val content : String
)
