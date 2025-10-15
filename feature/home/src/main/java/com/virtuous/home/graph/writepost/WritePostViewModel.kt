package com.virtuous.home.graph.writepost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtuous.domain.model.post.PostDetail
import com.virtuous.domain.model.post.WritePostType
import com.virtuous.domain.repository.PostRepository
import com.virtuous.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WritePostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _eventChannel = Channel<WritePostEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    private val _type: MutableStateFlow<WritePostType> = MutableStateFlow(WritePostType.GOOD_DEED)
    val type = _type.asStateFlow()

    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _content = MutableStateFlow("")
    val content = _content.asStateFlow()

    private val _images: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val images = _images.asStateFlow()

    private val _isVerified = MutableStateFlow(true)
    val isVerified = _isVerified.asStateFlow()

    private val _isCreatingPost = MutableStateFlow(false)
    val isCreatingPost = _isCreatingPost.asStateFlow()

    private val _isVerifyingPost = MutableStateFlow(false)
    val isVerifyingPost = _isVerifyingPost.asStateFlow()

    private val _showVerifyFailureDg = MutableStateFlow(false)
    val showVerifyFailureDialog = _showVerifyFailureDg.asStateFlow()

    fun setType(type: WritePostType) {
        _type.value = type
    }

    fun setTitle(title: String) {
        _title.value = title
    }

    fun setContent(content: String) {
        _content.value = content
    }

    fun setIsVerified(isVerified: Boolean) {
        _isVerified.value = isVerified
    }

    fun addImages(images: List<String>) {
        _images.value += images
    }

    fun removeImage(image: String) {
        _images.value = _images.value.filter { it != image }
    }

    fun setShowVerifyFailureDialog(show: Boolean) {
        _showVerifyFailureDg.value = show
    }

    fun addPost() = viewModelScope.launch {
        _isCreatingPost.value = true

        postRepository.addPost(
            _type.value,
            _title.value,
            _content.value,
            _images.value
        ).onSuccess { postDetail ->
            _eventChannel.send(WritePostEvent.NavigateToPostDetail(postDetail))
            _eventChannel.send(WritePostEvent.ShowSnackbar("게시글이 등록되었습니다."))
        }.onFailure {
            _eventChannel.send(WritePostEvent.ShowSnackbar("게시글 등록에 실패했습니다."))
        }

        _isCreatingPost.value = false
    }

    fun verifyAndAddPost() = viewModelScope.launch {
        _isVerifyingPost.value = true

        postRepository.verifyAndAddPost(
            _title.value,
            _content.value,
            _images.value
        ).onSuccess { postDetail ->
            _eventChannel.send(WritePostEvent.NavigateToPostDetail(postDetail))
            _eventChannel.send(WritePostEvent.ShowSnackbar("게시글이 등록되었습니다."))
            userRepository.loadMyUserInfo()
        }.onFailure {
            setShowVerifyFailureDialog(true)
        }

        _isVerifyingPost.value = false
    }

    sealed class WritePostEvent {
        data object NavigateToBack : WritePostEvent()
        data class NavigateToPostDetail(val postDetail: PostDetail) : WritePostEvent()
        data class ShowSnackbar(val message: String) : WritePostEvent()
    }
}