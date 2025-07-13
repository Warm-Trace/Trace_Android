package com.virtuous.network.model.post

import com.virtuous.domain.model.post.PostFeed
import com.virtuous.domain.model.post.PostType
import com.virtuous.network.model.cursor.Cursor
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class GetPostsResponse(
    val hasNext: Boolean,
    val cursor: Cursor?,
    val content: List<PostContent>,
) {
    fun toDomain(): List<PostFeed> = content.map { it.toDomain() }
}

@Serializable
data class PostContent(
    val postId: Int,
    val providerId: String,
    val postType: String,
    val title: String,
    val content: String,
    val nickname: String,
    val profileImageUrl: String? = null,
    val imageUrl: String? = null,
    val viewCount: Int,
    val commentCount: Int,
    val emotionCountSum : Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val isVerified: Boolean
) {
    fun toDomain(): PostFeed =
        PostFeed(
            postId = postId,
            providerId = providerId,
            postType = PostType.fromString(postType),
            title = title,
            content = content,
            nickname = nickname,
            profileImageUrl = profileImageUrl,
            viewCount = viewCount,
            commentCount = commentCount,
            totalEmotionCount = emotionCountSum,
            isVerified = isVerified,
            imageUrl = imageUrl,
            createdAt = createdAt.toJavaLocalDateTime(),
            updatedAt = updatedAt.toJavaLocalDateTime()
        )
}

