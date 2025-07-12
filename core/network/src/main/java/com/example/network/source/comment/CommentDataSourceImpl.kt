package com.example.network.source.comment

import com.example.network.api.TraceApi
import com.example.network.model.comment.AddCommentRequest
import com.example.network.model.comment.AddReplyToCommentRequest
import com.example.network.model.comment.CommentResponse
import com.example.network.model.comment.GetCommentsRequest
import com.example.network.model.comment.GetCommentsResponse
import com.example.network.model.report.ReportContentRequest
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject

class CommentDataSourceImpl @Inject constructor(
    private val traceApi: TraceApi,
) : CommentDataSource {
    override suspend fun getComments(
        postId: Int,
        cursorDateTime: LocalDateTime?,
        cursorId: Int?,
        size: Int
    ): Result<GetCommentsResponse> =
        traceApi.getComments(
            postId = postId,
            getCommentsRequest = GetCommentsRequest(
                cursorDateTime = cursorDateTime,
                cursorId = cursorId,
                size = size,
            )
        )

    override suspend fun addComment(postId: Int, content: String): Result<CommentResponse> =
        traceApi.addComment(
            postId = postId, addCommentRequest = AddCommentRequest(content)
        )

    override suspend fun addReplyToComment(
        postId: Int,
        commentId: Int,
        content: String
    ): Result<CommentResponse> = traceApi.addReplyToComment(
        postId = postId,
        commentId = commentId,
        addReplyToCommentRequest = AddReplyToCommentRequest(content)
    )

    override suspend fun deleteComment(commentId: Int): Result<Unit> =
        traceApi.deleteComment(commentId)

    override suspend fun reportComment(commentId: Int, reason: String): Result<Unit> =
        traceApi.reportContent(
            reportContentRequest = ReportContentRequest(
                postId = null,
                commentId = commentId,
                reason = reason,
            )
        )
}