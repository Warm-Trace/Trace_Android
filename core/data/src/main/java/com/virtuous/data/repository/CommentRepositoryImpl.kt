package com.virtuous.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.virtuous.common.util.suspendRunCatching
import com.virtuous.data.paging.CommentPagingSource
import com.virtuous.domain.model.post.Comment
import com.virtuous.domain.repository.CommentRepository
import com.virtuous.domain.repository.CommentUpdateEvent
import com.virtuous.network.source.comment.CommentDataSource
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.datetime.toJavaLocalDateTime
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val commentDataSource: CommentDataSource,
) : CommentRepository {
    private val _commentUpdateEvents = MutableSharedFlow<CommentUpdateEvent>(
        replay = 1,
        extraBufferCapacity = 32,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val commentUpdateEvents: SharedFlow<CommentUpdateEvent> =
        _commentUpdateEvents.asSharedFlow()

    override fun getCommentPagingFlow(postId: Int): Flow<PagingData<Comment>> {
        return Pager(
            config = PagingConfig(pageSize = DEFAULT_PAGE_SIZE),
            pagingSourceFactory = {
                CommentPagingSource(commentDataSource, postId)
            }
        ).flow
    }

    override suspend fun addComment(postId: Int, content: String): Result<Comment> =
        suspendRunCatching {
            val response = commentDataSource.addComment(postId = postId, content = content).getOrThrow()
            _commentUpdateEvents.emit(CommentUpdateEvent.CommentAdded(postId))

            Comment(
                postId = response.postId,
                providerId = response.providerId,
                commentId = response.commentId,
                parentId = response.parentId,
                nickName = response.nickName,
                profileImageUrl = response.userProfileImageUrl,
                content = response.content,
                createdAt = response.createdAt.toJavaLocalDateTime(),
                isDeleted = response.isDeleted,
                isOwner = response.isOwner,
                replies = emptyList()
            )
        }

    override suspend fun addReplyToComment(
        postId: Int,
        commentId: Int,
        content: String
    ): Result<Comment> = suspendRunCatching {
        val response = commentDataSource.addReplyToComment(
            postId = postId,
            commentId = commentId,
            content = content,
        ).getOrThrow()
        _commentUpdateEvents.emit(CommentUpdateEvent.CommentAdded(postId))

        Comment(
            postId = response.postId,
            providerId = response.providerId,
            commentId = response.commentId,
            parentId = response.parentId,
            nickName = response.nickName,
            profileImageUrl = response.userProfileImageUrl,
            content = response.content,
            createdAt = response.createdAt.toJavaLocalDateTime(),
            isDeleted = response.isDeleted,
            isOwner = response.isOwner,
            replies = emptyList()
        )
    }

    override suspend fun deleteComment(postId: Int, commentId: Int): Result<Unit> =
        suspendRunCatching {
            commentDataSource.deleteComment(commentId = commentId)
            _commentUpdateEvents.emit(CommentUpdateEvent.CommentDeleted(postId))
        }

    override suspend fun reportComment(commentId: Int, reason: String): Result<Unit> =
        suspendRunCatching {
            commentDataSource.reportComment(commentId, reason)
        }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 30
    }
}