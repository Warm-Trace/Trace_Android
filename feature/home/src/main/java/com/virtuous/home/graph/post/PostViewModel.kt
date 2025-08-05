package com.virtuous.home.graph.post

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.virtuous.domain.model.post.Comment
import com.virtuous.domain.model.post.Emotion
import com.virtuous.domain.model.post.EmotionCount
import com.virtuous.domain.model.post.PostDetail
import com.virtuous.domain.model.post.PostType
import com.virtuous.domain.repository.CommentRepository
import com.virtuous.domain.repository.PostRepository
import com.virtuous.navigation.HomeGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val savedStateHandle: SavedStateHandle,
    val eventHelper: com.virtuous.common_ui.event.EventHelper
) : ViewModel() {
    private val _eventChannel = Channel<PostEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    private val routeArgs: HomeGraph.PostRoute = savedStateHandle.toRoute()
    private val postId: Int = routeArgs.postId

    init {
        getPost()
    }

    private val _commentRefreshTrigger = MutableStateFlow(false)

    private val _postDetail = MutableStateFlow(
        PostDetail(
            postId = routeArgs.postId,
            postType = PostType.fromString(routeArgs.postType),
            viewCount = routeArgs.viewCount,
            emotionCount = EmotionCount(),
            title = routeArgs.title,
            content = routeArgs.content,
            missionContent = routeArgs.missionContent,
            providerId = routeArgs.providerId,
            nickname = routeArgs.nickname,
            images = when {
                routeArgs.imageUrl != null -> listOf(routeArgs.imageUrl!!)
                else -> emptyList()
            },
            profileImageUrl = routeArgs.profileImageUrl,
            yourEmotionType = Emotion.fromString(routeArgs.yourEmotionType),
            createdAt = if (routeArgs.createdAt.isNotEmpty()) LocalDateTime.parse(routeArgs.createdAt) else LocalDateTime.now(),
            updatedAt = if (routeArgs.createdAt.isNotEmpty()) LocalDateTime.parse(routeArgs.createdAt) else LocalDateTime.now(),
            isOwner = routeArgs.isOwner,
            isVerified = routeArgs.isVerified
        )
    )
    val postDetail = _postDetail.asStateFlow()

    private val _deletedCommentIds = MutableStateFlow<Set<Int>>(emptySet())

    private val _cachedComments = _commentRefreshTrigger
        .flatMapLatest {
            commentRepository.getCommentPagingFlow(postId)
        }
        .cachedIn(viewModelScope)

    val comments: Flow<PagingData<Comment>> =
        combine(
            _cachedComments,
            _deletedCommentIds,
        ) { pagingData, deletedIds ->
            pagingData
                .filter { comment ->
                    // 대댓글이 없는 부모 댓글이고, 삭제된 경우 -> 목록에서 완전히 제거
                    (comment.replies.isEmpty() && comment.commentId in deletedIds).not()
                }
                .map { comment ->
                    // 삭제된 대댓글을 replies 리스트에서 필터링
                    val filteredReplies = comment.replies.filter { reply ->
                        reply.commentId !in deletedIds
                    }

                    var newComment = comment.copy(replies = filteredReplies)

                    // 대댓글이 있는 부모 댓글이고, 삭제된 경우 -> isDeleted = true로 변경
                    if (comment.replies.isNotEmpty() && comment.commentId in deletedIds) {
                        newComment = newComment.copy(isDeleted = true)
                    }

                    newComment
                }
        }

    private val _commentInput = MutableStateFlow("")
    val commentInput = _commentInput.asStateFlow()

    private val _replyTargetId: MutableStateFlow<Int?> = MutableStateFlow(null)
    val replyTargetId = _replyTargetId.asStateFlow()

    fun onRefresh() {
        getPost()
        _deletedCommentIds.value = emptySet()
    }

    fun setCommentInput(commentInput: String) {
        _commentInput.value = commentInput
    }

    fun setReplyTargetId(commentId: Int) {
        _replyTargetId.value = commentId
    }

    fun clearReplyTargetId() {
        _replyTargetId.value = null
    }

    private fun getPost() = viewModelScope.launch {
        delay(300) // 화면 전환 애니메이션 버벅임 방지

        postRepository.getPost(postId).onSuccess {
            _postDetail.value = it
        }.onFailure {
            _eventChannel.send(PostEvent.GetPostFailure)
        }
    }

    fun reportPost(reason: String) = viewModelScope.launch {
        postRepository.reportPost(postId, reason)
            .onSuccess { _eventChannel.send(PostEvent.ReportPostSuccess) }
            .onFailure { _eventChannel.send(PostEvent.ReportPostFailure) }
    }

    fun blockUser(providerId: String) = viewModelScope.launch {
        postRepository.blockUser(providerId)
            .onSuccess { _eventChannel.send(PostEvent.BlockUserSuccess) }
            .onFailure { _eventChannel.send(PostEvent.BlockUserFailure) }
    }

    fun deletePost() = viewModelScope.launch {
        postRepository.deletePost(postId = postId)
            .onSuccess { _eventChannel.send(PostEvent.DeletePostSuccess) }
            .onFailure { _eventChannel.send(PostEvent.DeletePostFailure) }
    }

    fun toggleEmotion(emotion: Emotion) = viewModelScope.launch {
        postRepository.toggleEmotion(postId = postId, emotionType = emotion).onSuccess { isAdded ->
            val current = _postDetail.value

            val updatedEmotionCount = current.emotionCount.let { count ->
                var result = count

                if (emotion != _postDetail.value.yourEmotionType && _postDetail.value.yourEmotionType != null) {
                    result = when (_postDetail.value.yourEmotionType) {
                        Emotion.HEARTWARMING -> result.copy(heartWarmingCount = result.heartWarmingCount - 1)
                        Emotion.LIKEABLE -> result.copy(likeableCount = result.likeableCount - 1)
                        Emotion.TOUCHING -> result.copy(touchingCount = result.touchingCount - 1)
                        Emotion.IMPRESSIVE -> result.copy(impressiveCount = result.impressiveCount - 1)
                        Emotion.GRATEFUL -> result.copy(gratefulCount = result.gratefulCount - 1)
                        else -> result
                    }
                }

                result = when (emotion) {
                    Emotion.HEARTWARMING -> result.copy(heartWarmingCount = result.heartWarmingCount + if (isAdded) 1 else -1)
                    Emotion.LIKEABLE -> result.copy(likeableCount = result.likeableCount + if (isAdded) 1 else -1)
                    Emotion.TOUCHING -> result.copy(touchingCount = result.touchingCount + if (isAdded) 1 else -1)
                    Emotion.IMPRESSIVE -> result.copy(impressiveCount = result.impressiveCount + if (isAdded) 1 else -1)
                    Emotion.GRATEFUL -> result.copy(gratefulCount = result.gratefulCount + if (isAdded) 1 else -1)
                }

                result
            }

            _postDetail.value =
                current.copy(
                    yourEmotionType = if (isAdded) emotion else null,
                    emotionCount = updatedEmotionCount
                )
        }
    }

    private fun refreshComments() = viewModelScope.launch {
        _commentRefreshTrigger.value = !_commentRefreshTrigger.value
    }

    fun addComment() = viewModelScope.launch {
        if (_commentInput.value.isEmpty()) {
            _eventChannel.send(PostEvent.ShowSnackBar("내용을 입력해주세요."))
            return@launch
        }

        commentRepository.addComment(postId = postId, content = _commentInput.value)
            .onSuccess {
                _commentInput.value = ""
                refreshComments()
                _eventChannel.send(PostEvent.AddCommentSuccess)
            }.onFailure {
                _eventChannel.send(PostEvent.AddCommentFailure)
            }
    }

    fun replyComment(onSuccess: (Int) -> Unit) =
        viewModelScope.launch {
            val parentId = _replyTargetId.value ?: return@launch

            if (_commentInput.value.isEmpty()) {
                _eventChannel.send(PostEvent.ShowSnackBar("내용을 입력해주세요."))
                return@launch
            }

            commentRepository.addReplyToComment(
                postId = postId,
                commentId = parentId,
                content = _commentInput.value
            ).onSuccess {
                clearReplyTargetId()
                _commentInput.value = ""
                refreshComments()
                onSuccess(parentId)
                _eventChannel.send(PostEvent.AddReplySuccess)
            }.onFailure {
                _eventChannel.send(PostEvent.AddReplyFailure)
            }
        }


    fun deleteComment(commentId: Int) = viewModelScope.launch {
        commentRepository.deleteComment(commentId)
            .onSuccess {
                _eventChannel.send(PostEvent.DeleteCommentSuccess)
                _deletedCommentIds.value += commentId
            }
            .onFailure { _eventChannel.send(PostEvent.DeleteCommentFailure) }
    }

    fun reportComment(commentId: Int, reason: String) = viewModelScope.launch {
        commentRepository.reportComment(commentId, reason)
            .onSuccess { _eventChannel.send(PostEvent.ReportCommentSuccess) }
            .onFailure { _eventChannel.send(PostEvent.ReportCommentFailure) }
    }

    sealed class PostEvent {
        data class ShowSnackBar(val message: String) : PostEvent()
        data object GetPostFailure : PostEvent()
        data object DeletePostSuccess : PostEvent()
        data object DeletePostFailure : PostEvent()
        data object ReportPostSuccess : PostEvent()
        data object ReportPostFailure : PostEvent()
        data object ReportCommentSuccess : PostEvent()
        data object ReportCommentFailure : PostEvent()
        data object AddCommentSuccess : PostEvent()
        data object AddCommentFailure : PostEvent()
        data object AddReplySuccess : PostEvent()
        data object AddReplyFailure : PostEvent()
        data object DeleteCommentSuccess : PostEvent()
        data object DeleteCommentFailure : PostEvent()
        data object BlockUserSuccess : PostEvent()
        data object BlockUserFailure : PostEvent()
    }
}
