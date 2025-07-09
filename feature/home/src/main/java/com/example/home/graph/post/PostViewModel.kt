package com.example.home.graph.post

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.common.event.EventHelper
import com.example.common.event.TraceEvent
import com.example.domain.model.post.Emotion
import com.example.domain.model.post.EmotionCount
import com.example.domain.model.post.PostDetail
import com.example.domain.model.post.PostType
import com.example.domain.repository.CommentRepository
import com.example.domain.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    val eventHelper: EventHelper
) : ViewModel() {
    private val _eventChannel = Channel<PostEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    private val postId: Int = requireNotNull(savedStateHandle["postId"])

    init {
        getPost()
    }

    private val _refreshTrigger = MutableStateFlow(false)

    private val _postDetail = MutableStateFlow(
        PostDetail(
            postId = -1,
            postType = PostType.GOOD_DEED,
            viewCount = 0,
            emotionCount = EmotionCount(),
            title = "",
            content = "",
            missionContent = "",
            providerId = "",
            nickname = "",
            images = emptyList(),
            profileImageUrl = "",
            yourEmotionType = null,
            createdAt = LocalDateTime.MIN,
            updatedAt =  LocalDateTime.MIN,
            isOwner = false,
            isVerified = false
        )
    )
    val postDetail = _postDetail.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val commentPagingFlow = _refreshTrigger
        .flatMapLatest {
            commentRepository.getCommentPagingFlow(postId)
        }
        .cachedIn(viewModelScope)

    private val _commentInput = MutableStateFlow("")
    val commentInput = _commentInput.asStateFlow()

    private val _replyTargetId: MutableStateFlow<Int?> = MutableStateFlow(null)
    val replyTargetId = _replyTargetId.asStateFlow()

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
        }
    }

    fun reportPost() {}

    fun deletePost() = viewModelScope.launch {
        postRepository.deletePost(postId = postId).onSuccess {
            _eventChannel.send(PostEvent.DeletePostSuccess)
        }.onFailure {
            _eventChannel.send(PostEvent.DeletePostFailure)
        }
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
        _refreshTrigger.value = !_refreshTrigger.value
    }

    fun addComment() = viewModelScope.launch {
        if (_commentInput.value.isEmpty()) {
            eventHelper.sendEvent(TraceEvent.ShowSnackBar("내용을 입력해주세요."))
            return@launch
        }

        commentRepository.addComment(postId = postId, content = _commentInput.value)
            .onSuccess { comment ->
                _commentInput.value = ""
                refreshComments()
            }.onFailure {
                eventHelper.sendEvent(TraceEvent.ShowSnackBar("댓글 작성에 실패했습니다."))
            }

    }

    fun replyComment(onSuccess: (Int) -> Unit) =
        viewModelScope.launch {
            val parentId = _replyTargetId.value ?: return@launch

            if (_commentInput.value.isEmpty()) {
                eventHelper.sendEvent(TraceEvent.ShowSnackBar("내용을 입력해주세요."))
                return@launch
            }

            commentRepository.addReplyToComment(
                postId = postId,
                commentId = parentId,
                content = _commentInput.value
            ).onSuccess { replyComment ->
                clearReplyTargetId()
                refreshComments()
                _commentInput.value = ""

                onSuccess(parentId)
            }.onFailure {
                eventHelper.sendEvent(TraceEvent.ShowSnackBar("답글 작성에 실패했습니다."))
            }

        }


    fun deleteComment(commentId: Int) = viewModelScope.launch {
        commentRepository.deleteComment(commentId).onSuccess {
            refreshComments()
        }.onFailure {
            eventHelper.sendEvent(TraceEvent.ShowSnackBar("댓글 삭제에 실패했습니다."))
        }

    }

    fun reportComment(commentId: Int) {}

    sealed class PostEvent {
        data object DeletePostSuccess : PostEvent()
        data object DeletePostFailure : PostEvent()
    }

}

