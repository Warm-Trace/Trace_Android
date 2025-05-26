package com.example.domain.repository

import androidx.paging.PagingData
import com.example.domain.model.post.Comment
import kotlinx.coroutines.flow.Flow

interface CommentRepository {
    fun getCommentPagingFlow(postId: Int): Flow<PagingData<Comment>>

    suspend fun addComment(postId: Int, content: String): Result<Comment>

    suspend fun addReplyToComment(postId: Int, commentId: Int, content: String): Result<Comment>

    suspend fun deleteComment(commentId: Int): Result<Unit>
}