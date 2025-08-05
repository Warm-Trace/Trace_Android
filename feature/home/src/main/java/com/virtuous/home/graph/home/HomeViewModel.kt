package com.virtuous.home.graph.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.insertHeaderItem
import androidx.paging.map
import com.virtuous.domain.model.post.HomeTab
import com.virtuous.domain.model.post.PostFeed
import com.virtuous.domain.repository.PostRepository
import com.virtuous.domain.repository.PostUpdateEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postRepository: PostRepository,
) : ViewModel() {
    private val _eventChannel = Channel<HomeEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    internal fun onEvent(event: HomeEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    init {
        viewModelScope.launch {
            postRepository.postUpdateEvents.collect { event ->
                when (event) {
                    is PostUpdateEvent.PostDeleted -> {
                        _deletedPostIds.value += event.postId
                    }

                    is PostUpdateEvent.UserBlocked -> {
                        _blockedProviderIds.value += event.providerId
                    }

                    is PostUpdateEvent.PostUpdated -> {
                        _updatedPostFeeds.value += event.postFeed
                    }

                    is PostUpdateEvent.PostAdded -> {
                        _addedPostFeeds.value += event.postFeed
                    }
                }
            }
        }
    }


    private val _tabType: MutableStateFlow<HomeTab> = MutableStateFlow(HomeTab.ALL)
    val tabType = _tabType.asStateFlow()

    private val _deletedPostIds = MutableStateFlow<Set<Int>>(emptySet())
    private val _blockedProviderIds = MutableStateFlow<Set<String>>(emptySet())
    private val _addedPostFeeds = MutableStateFlow<List<PostFeed>>(emptyList())
    private val _updatedPostFeeds = MutableStateFlow<List<PostFeed>>(emptyList())

    private val _cachedPostFeeds = tabType.flatMapLatest { tab ->
        postRepository.getPosts(tab)
    }.cachedIn(viewModelScope)

    val postFeeds: Flow<PagingData<PostFeed>> =
        combine(
            _cachedPostFeeds,
            _deletedPostIds,
            _blockedProviderIds,
            _addedPostFeeds,
            _updatedPostFeeds
        ) { pagingData, deletedIds, blockedIds, addedPostFeeds, updatedPostFeeds ->
            var result = pagingData
                .filter { it.providerId !in blockedIds && it.postId !in deletedIds }
                .map { postFeed ->
                    val updatedPost = updatedPostFeeds.find { it.postId == postFeed.postId }
                    updatedPost ?: postFeed
                }

            addedPostFeeds.reversed().forEach {
                result = result.insertHeaderItem(item = it)
            }

            result
        }

    fun onRefresh() {
        _deletedPostIds.value = emptySet()
        _blockedProviderIds.value = emptySet()
        _addedPostFeeds.value = emptyList()
    }

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

