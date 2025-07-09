package com.example.home.graph.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.domain.model.post.HomeTab
import com.example.domain.model.post.PostFeed
import com.example.domain.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _tabType: MutableStateFlow<HomeTab> = MutableStateFlow(HomeTab.ALL)
    val tabType = _tabType.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val postFeeds = tabType
        .flatMapLatest { tab ->
            postRepository.getPosts(tab)
        }
        .cachedIn(viewModelScope)

    fun setTabType(tabType: HomeTab) {
        _tabType.value = tabType
    }

    sealed class HomeEvent {
        data class NavigateToPost(val postFeed: PostFeed) : HomeEvent()
        data object NavigateToWritePost : HomeEvent()
        data object NavigateToSearch : HomeEvent()
    }
}



