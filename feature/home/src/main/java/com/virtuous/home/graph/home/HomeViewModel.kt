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
import com.virtuous.domain.repository.CommentRepository
import com.virtuous.domain.repository.CommentUpdateEvent
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
    private val commentRepository: CommentRepository
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

                    is PostUpdateEvent.EmotionAdded -> {
                        val currentUpdates = _emotionCountUpdates.value.toMutableMap()
                        currentUpdates[event.postId] = (currentUpdates[event.postId] ?: 0) + 1
                        _emotionCountUpdates.value = currentUpdates
                    }

                    is PostUpdateEvent.EmotionDeleted -> {
                        val currentUpdates = _emotionCountUpdates.value.toMutableMap()
                        currentUpdates[event.postId] = (currentUpdates[event.postId] ?: 0) - 1
                        _emotionCountUpdates.value = currentUpdates
                    }
                }
            }
        }

        viewModelScope.launch {
            commentRepository.commentUpdateEvents.collect { event ->
                when (event) {
                    is CommentUpdateEvent.CommentDeleted -> {
                        val currentUpdates = _commentCountUpdates.value.toMutableMap()
                        currentUpdates[event.postId] = (currentUpdates[event.postId] ?: 0) - 1
                        _commentCountUpdates.value = currentUpdates
                    }

                    is CommentUpdateEvent.CommentAdded -> {
                        val currentUpdates = _commentCountUpdates.value.toMutableMap()
                        currentUpdates[event.postId] = (currentUpdates[event.postId] ?: 0) + 1
                        _commentCountUpdates.value = currentUpdates
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
    private val _commentCountUpdates = MutableStateFlow<Map<Int, Int>>(emptyMap())
    private val _emotionCountUpdates = MutableStateFlow<Map<Int, Int>>(emptyMap())

    private val _cachedPostFeeds = tabType.flatMapLatest { tab ->
        postRepository.getPosts(tab)
    }.cachedIn(viewModelScope)

    private val _combinedPostFlows = combine(
        _cachedPostFeeds,
        _deletedPostIds,
        _blockedProviderIds,
        _addedPostFeeds,
        _updatedPostFeeds
    ) { pagingData, deletedIds, blockedIds, addedPostFeeds, updatedPostFeeds ->
        Quintuple(pagingData, deletedIds, blockedIds, addedPostFeeds, updatedPostFeeds)
    }

    val postFeeds: Flow<PagingData<PostFeed>> = combine(
        _combinedPostFlows,
        _commentCountUpdates,
        _emotionCountUpdates
    ) { (pagingData, deletedIds, blockedIds, addedPostFeeds, updatedPostFeeds), commentCountUpdates, emotionCountUpdates ->
        var result = pagingData
            .filter { it.providerId !in blockedIds && it.postId !in deletedIds }
            .map { postFeed ->
                val updatedPost = updatedPostFeeds.find { it.postId == postFeed.postId } ?: postFeed
                val commentCountChange = commentCountUpdates[postFeed.postId] ?: 0
                val emotionCountChange = emotionCountUpdates[postFeed.postId] ?: 0
                updatedPost.copy(
                    commentCount = (updatedPost.commentCount + commentCountChange).coerceAtLeast(0),
                    totalEmotionCount = (updatedPost.totalEmotionCount + emotionCountChange).coerceAtLeast(0)
                )
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
        _updatedPostFeeds.value = emptyList()
        _commentCountUpdates.value = emptyMap()
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

    data class Quintuple<A, B, C, D, E>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D,
        val fifth: E
    )
}
