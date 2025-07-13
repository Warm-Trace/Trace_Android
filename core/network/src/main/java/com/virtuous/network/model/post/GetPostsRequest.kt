package com.virtuous.network.model.post

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class GetPostsRequest(
    val cursorDateTime : LocalDateTime?,
    val cursorId : Int?,
    val size : Int,
    val postType : String?
)
