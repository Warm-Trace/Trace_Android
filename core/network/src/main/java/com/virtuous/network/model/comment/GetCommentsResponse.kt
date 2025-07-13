package com.virtuous.network.model.comment

import com.virtuous.domain.model.post.Comment
import com.virtuous.network.model.cursor.Cursor
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class GetCommentsResponse(
    val hasNext: Boolean,
    val cursor: Cursor?,
    val content: List<CommentContent>,
) {
    fun toDomain(): List<Comment> = content.map { it.toDomain() }
}

@Serializable
data class CommentContent(
    val postId: Int,
    val providerId: String,
    val commentId: Int,
    val parentId: Int?,
    val nickName: String,
    val userProfileImageUrl: String?,
    val content: String,
    val createdAt: LocalDateTime,
    val children: List<CommentContent>,
    val parent: Boolean,
    val isDeleted: Boolean,
    val isOwner: Boolean,
) {
    fun toDomain(): Comment =
        Comment(
            postId = postId,
            providerId = providerId,
            commentId = commentId,
            parentId = parentId,
            nickName = nickName,
            profileImageUrl = userProfileImageUrl,
            content = content,
            createdAt = createdAt.toJavaLocalDateTime(),
            isDeleted = isDeleted,
            isOwner = isOwner,
            replies = children.map {
                it.toDomain()
            }
        )
}
