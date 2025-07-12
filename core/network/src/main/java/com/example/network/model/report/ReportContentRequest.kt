package com.example.network.model.report

import kotlinx.serialization.Serializable

@Serializable
data class ReportContentRequest(
    val postId : Int?,
    val commentId : Int?,
    val reason : String
)
