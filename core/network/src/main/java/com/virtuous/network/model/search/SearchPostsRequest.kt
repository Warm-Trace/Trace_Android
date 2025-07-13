package com.virtuous.network.model.search

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class SearchPostsRequest(
    val cursorDateTime : LocalDateTime?,
    val cursorId : Int?,
    val size : Int,
    val keyword : String,
    val postType : String?,
    val searchType : String
)
