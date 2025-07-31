package com.virtuous.network.model.post

import kotlinx.datetime.LocalDateTime

data class GetUserPostsRequest(
    val cursorDateTime : LocalDateTime?,
    val cursorId : Int?,
    val size : Int,
    val myPageTab : String
)
