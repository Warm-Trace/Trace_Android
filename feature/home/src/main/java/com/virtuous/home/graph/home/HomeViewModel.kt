package com.virtuous.home.graph.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.insertHeaderItem
import androidx.paging.map
import com.virtuous.analytics.AnalyticsHelper
import com.virtuous.domain.model.post.HomeTab
import com.virtuous.domain.model.post.PostFeed
import com.virtuous.domain.repository.CommentRepository
import com.virtuous.domain.repository.CommentUpdateEvent
import com.virtuous.domain.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val analyticsHelper: AnalyticsHelper,
) : ViewModel() {

    private val _eventChannel = Channel<HomeEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    private val _tabType: MutableStateFlow<HomeTab> = MutableStateFlow(HomeTab.ALL)
    val tabType = _tabType.asStateFlow()

    private val cachedPostFeeds = tabType.flatMapLatest { tab ->
        postRepository.getPosts(tab)
    }.cachedIn(viewModelScope)

    private val postFeedUpdates = MutableStateFlow(PostFeedUpdates())
    val postFeeds = combine(
        cachedPostFeeds,
        postFeedUpdates
    ) { pagingData, updates ->
        var result = pagingData
            .filter { post ->
                post.postId !in updates.deletedIds && post.providerId !in updates.blockedIds
            }.map { post ->
                val updatedPost = updates.updatedPostFeeds[post.postId]
                val updatedEmotionCount = updates.emotionCountUpdates[post.postId]
                val updatedCommentCount = updates.commentCountUpdates[post.postId]

                var currentPost = updatedPost ?: post
                if (updatedEmotionCount != null) {
                    currentPost =
                        currentPost.copy(totalEmotionCount = currentPost.totalEmotionCount + updatedEmotionCount)
                }
                if (updatedCommentCount != null) {
                    currentPost = currentPost.copy(commentCount = currentPost.commentCount + updatedCommentCount)
                }

                currentPost
            }

        updates.addedPostFeeds.reversed().forEach {
            result = result.insertHeaderItem(item = it)
        }

        result
    }

    init {
        viewModelScope.launch {
            postRepository.postUpdateEvents.collect { event ->
                postFeedUpdates.update { currentUpdates ->
                    when (event) {
                        is PostRepository.PostUpdateEvent.PostDeleted ->
                            currentUpdates.copy(deletedIds = currentUpdates.deletedIds + event.postId)

                        is PostRepository.PostUpdateEvent.UserBlocked ->
                            currentUpdates.copy(blockedIds = currentUpdates.blockedIds + event.providerId)

                        is PostRepository.PostUpdateEvent.PostUpdated ->
                            currentUpdates.copy(updatedPostFeeds = currentUpdates.updatedPostFeeds + (event.postFeed.postId to event.postFeed))

                        is PostRepository.PostUpdateEvent.PostAdded -> {
                            currentUpdates.copy(addedPostFeeds = currentUpdates.addedPostFeeds + event.postFeed)
                        }

                        is PostRepository.PostUpdateEvent.EmotionAdded -> {
                            val currentCounts = currentUpdates.emotionCountUpdates
                            val currentCount = currentCounts[event.postId] ?: 0
                            val newCount = currentCount + 1
                            val newMap = currentCounts + (event.postId to newCount)
                            currentUpdates.copy(emotionCountUpdates = newMap)
                        }

                        is PostRepository.PostUpdateEvent.EmotionDeleted -> {
                            val currentCounts = currentUpdates.emotionCountUpdates
                            val currentCount = currentCounts[event.postId] ?: 0
                            val newCount = currentCount - 1
                            val newMap = currentCounts + (event.postId to newCount)
                            currentUpdates.copy(emotionCountUpdates = newMap)
                        }
                    }
                }
            }
        }

        viewModelScope.launch {
            commentRepository.commentUpdateEvents.collect { event ->
                postFeedUpdates.update { currentUpdates ->
                    when (event) {
                        is CommentUpdateEvent.CommentAdded -> {
                            val currentCounts = currentUpdates.commentCountUpdates
                            val currentCount = currentCounts[event.postId] ?: 0
                            val newCount = currentCount + 1
                            val newMap = currentCounts + (event.postId to newCount)
                            currentUpdates.copy(commentCountUpdates = newMap)
                        }

                        is CommentUpdateEvent.CommentDeleted -> {
                            val currentCounts = currentUpdates.commentCountUpdates
                            val currentCount = currentCounts[event.postId] ?: 0
                            val newCount = currentCount - 1
                            val newMap = currentCounts + (event.postId to newCount)
                            currentUpdates.copy(commentCountUpdates = newMap)
                        }
                    }
                }
            }
        }
    }

    fun setTabType(tabType: HomeTab) {
        _tabType.value = tabType
        viewModelScope.launch {
            analyticsHelper.trackActionEvent(
                screenName = "home",
                actionName = "change_tab",
                properties = mutableMapOf("tab" to _tabType.value.name)
            )
        }
    }

    fun onRefresh() {
        postFeedUpdates.update { PostFeedUpdates() }
        viewModelScope.launch {
            analyticsHelper.trackActionEvent(
                screenName = "home",
                actionName = "refresh_post_feed",
            )
        }
    }

    sealed class HomeEvent {
        data class NavigateToPost(val postFeed: PostFeed) : HomeEvent()
        data object NavigateToNotification : HomeEvent()
        data object NavigateToWritePost : HomeEvent()
        data object NavigateToSearch : HomeEvent()
    }

    private data class PostFeedUpdates(
        val deletedIds: Set<Int> = emptySet(),
        val blockedIds: Set<String> = emptySet(),
        val addedPostFeeds: List<PostFeed> = emptyList(),
        val updatedPostFeeds: Map<Int, PostFeed> = emptyMap(),
        val emotionCountUpdates: Map<Int, Int> = emptyMap(),
        val commentCountUpdates: Map<Int, Int> = emptyMap()
    )
}

