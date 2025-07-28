package com.virtuous.network.model.post

import kotlinx.serialization.Serializable

@Serializable
data class UpdatePostRequest(
    val title: String,
    val content: String,
    val removal: List<String>,
)
