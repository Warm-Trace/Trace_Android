package com.virtuous.domain.repository

import androidx.paging.PagingData
import com.virtuous.domain.model.post.Comment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface CommentRepository {
    val commentUpdateEvents: SharedFlow<CommentUpdateEvent>

    fun getCommentPagingFlow(postId: Int): Flow<PagingData<Comment>>

    suspend fun addComment(postId: Int, content: String): Result<Comment>

    suspend fun addReplyToComment(postId: Int, commentId: Int, content: String): Result<Comment>

    suspend fun deleteComment(postId : Int, commentId: Int): Result<Unit>

    suspend fun reportComment(commentId: Int, reason: String): Result<Unit>
}

sealed class CommentUpdateEvent {
    data class CommentAdded(val postId: Int) : CommentUpdateEvent()
    data class CommentDeleted(val postId: Int) : CommentUpdateEvent()
}