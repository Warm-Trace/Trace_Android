package com.example.network.model.post

import kotlinx.serialization.Serializable

@Serializable
data class UpdatePostRequest(
    val title: String,
    val content : String,
)
