package com.example.network.source.comment

import com.example.network.model.comment.CommentResponse
import com.example.network.model.comment.GetCommentsResponse
import kotlinx.datetime.LocalDateTime

interface CommentDataSource {
    suspend fun getComments(
        postId: Int,
        cursorDateTime : LocalDateTime?,
        cursorId : Int?,
        size : Int,
    ) : Result<GetCommentsResponse>

    suspend fun addComment(
        postId : Int, content : String,
    ) : Result<CommentResponse>

    suspend fun addReplyToComment(
        postId: Int, commentId : Int, content : String,
    ) : Result<CommentResponse>

    suspend fun deleteComment(
        commentId: Int,
    ) : Result<Unit>

    suspend fun reportComment(
        commentId: Int,
        reason : String,
    ) : Result<Unit>
}