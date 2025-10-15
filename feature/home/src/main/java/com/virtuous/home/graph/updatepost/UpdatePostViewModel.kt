package com.virtuous.home.graph.updatepost

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.virtuous.domain.model.post.PostDetail
import com.virtuous.domain.model.post.PostType
import com.virtuous.domain.repository.PostRepository
import com.virtuous.navigation.HomeGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdatePostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _eventChannel = Channel<UpdatePostEvent>(Channel.BUFFERED)
    val eventChannel = _eventChannel.receiveAsFlow()

    private val routeArgs: HomeGraph.PostRoute = savedStateHandle.toRoute()
    private val postId = routeArgs.postId

    init {
        getPost()
    }

    private val _type: MutableStateFlow<PostType> = MutableStateFlow(PostType.GOOD_DEED)
    val type = _type.asStateFlow()

    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _content = MutableStateFlow("")
    val content = _content.asStateFlow()

    private val _images: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val images = _images.asStateFlow()

    private val _removedImages = mutableListOf<String>()

    private val _newImages = mutableListOf<String>()

    fun setTitle(title: String) {
        _title.value = title
    }

    fun setContent(content: String) {
        _content.value = content
    }

    fun setType(type: PostType) {
        _type.value = type
    }

    fun addImages(images: List<String>) {
        _images.value += images
        _newImages += images
    }

    fun removeImage(image: String) {
        _images.value = _images.value.filter { it != image }
        _newImages.remove(image)
        if (image.contains("http")) _removedImages += image
    }

    private fun getPost() = viewModelScope.launch {
        postRepository.getPost(postId).onSuccess { postDetail ->
            setType(postDetail.postType)
            setTitle(postDetail.title)
            setContent(postDetail.content)
            addImages(postDetail.images)
        }
    }

    fun updatePost() = viewModelScope.launch {
        postRepository.updatePost(
            postId = postId,
            title = _title.value,
            content = _content.value,
            removedImages = _removedImages,
            images = _newImages
        ).onSuccess { postDetail ->
            _eventChannel.send(UpdatePostEvent.NavigateToPostDetail(postDetail))
            _eventChannel.send(UpdatePostEvent.ShowSnackbar("게시글이 수정되었습니다."))
        }.onFailure {
            _eventChannel.send(UpdatePostEvent.ShowSnackbar("게시글 수정에 실패했습니다."))
        }
    }

    sealed class UpdatePostEvent {
        data object NavigateToBack : UpdatePostEvent()
        data class NavigateToPostDetail(val postDetail: PostDetail) : UpdatePostEvent()
        data class ShowSnackbar(val message: String) : UpdatePostEvent()
    }
}
