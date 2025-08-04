package com.virtuous.home.graph.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.virtuous.domain.model.post.HomeTab
import com.virtuous.domain.model.post.PostFeed
import com.virtuous.domain.repository.PostRepository
import com.virtuous.domain.repository.PostUpdateEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postRepository: PostRepository,
) : ViewModel() {
    private val _eventChannel = Channel<HomeEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    init {
        postRepository.postUpdateEvents
            .onEach { event ->
                when (event) {
                    is PostUpdateEvent.PostDeleted -> {
                        _deletedPostIds.value = _deletedPostIds.value + event.postId
                    }

                    is PostUpdateEvent.PostEdited -> {
                        // TODO : 수정 이벤트 처리
                    }
                }
            }.launchIn(viewModelScope)
    }

    internal fun onEvent(event: HomeEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    private val _tabType: MutableStateFlow<HomeTab> = MutableStateFlow(HomeTab.ALL)
    val tabType = _tabType.asStateFlow()

    private val _deletedPostIds = MutableStateFlow<Set<Int>>(emptySet())

    val postFeeds: kotlinx.coroutines.flow.Flow<PagingData<PostFeed>> =
        combine(
            tabType.flatMapLatest { tab ->
                postRepository.getPosts(tab)
            },
            _deletedPostIds
        ) { pagingData, deletedIds ->
            pagingData.filter { postFeed ->
                postFeed.postId !in deletedIds
            }
        }.cachedIn(viewModelScope)

    fun setTabType(tabType: HomeTab) {
        _tabType.value = tabType
    }

    sealed class HomeEvent {
        data class NavigateToPost(val postFeed: PostFeed) : HomeEvent()
        data object NavigateToNotification : HomeEvent()
        data object NavigateToWritePost : HomeEvent()
        data object NavigateToSearch : HomeEvent()
    }
}



