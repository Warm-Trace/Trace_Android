package com.example.home.graph.updatepost

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.event.EventHelper
import com.example.domain.model.post.PostType
import com.example.domain.repository.PostRepository
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
    val eventHelper: EventHelper
) : ViewModel() {
    private val _eventChannel = Channel<UpdatePostEvent>(Channel.BUFFERED)
    val eventChannel = _eventChannel.receiveAsFlow()

    private val postId: Int = requireNotNull(savedStateHandle["postId"])

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
        _images.value += _images.value + images
    }

    fun removeImage(image: String) {
        _images.value = _images.value.filter { it != image }
    }

    private fun getPost() = viewModelScope.launch {
            postRepository.getPost(postId).onSuccess { postDetail ->
                setType(postDetail.postType)
                setTitle(postDetail.title)
                setContent(postDetail.content)
                addImages(postDetail.images)
            }

    }

    // fun updatePost = viewModelScope.launch {  }

    sealed class UpdatePostEvent {
        data object NavigateToBack : UpdatePostEvent()
        data class UpdatePostSuccess(val postId: Int) : UpdatePostEvent()
        data object UpdatePostFailure : UpdatePostEvent()
    }
}
