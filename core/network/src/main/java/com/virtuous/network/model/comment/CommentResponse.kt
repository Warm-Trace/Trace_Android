package com.virtuous.network.model.comment

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class CommentResponse(
    val postId : Int,
    val providerId : String,
    val commentId : Int,
    val parentId: Int? = null,
    val nickName : String,
    val userProfileImageUrl : String,
    val content : String,
    val createdAt : LocalDateTime,
    val isDeleted : Boolean,
    val isOwner : Boolean,
)
