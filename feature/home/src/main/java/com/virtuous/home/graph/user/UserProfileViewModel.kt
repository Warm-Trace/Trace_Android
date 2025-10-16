package com.virtuous.home.graph.user

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.cachedIn
import com.virtuous.analytics.AnalyticsHelper
import com.virtuous.analytics.error.ErrorHelper
import com.virtuous.domain.model.home.UserProfileTab
import com.virtuous.domain.model.post.PostFeed
import com.virtuous.domain.model.user.UserInfo
import com.virtuous.domain.repository.PostRepository
import com.virtuous.domain.repository.UserRepository
import com.virtuous.navigation.HomeGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val savedStateHandle: SavedStateHandle,
    private val analyticsHelper: AnalyticsHelper,
    private val errorHelper: ErrorHelper,
) : ViewModel() {
    private val _eventChannel = Channel<UserProfileEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    internal fun onEvent(event: UserProfileEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    private val routeArgs: HomeGraph.UserProfileRoute = savedStateHandle.toRoute()
    private val providerId = routeArgs.providerId

    private val _userInfo = MutableStateFlow(
        UserInfo(
            "닉네임", null, 0, 0, 0,0
        )
    )
    val userInfo = _userInfo.asStateFlow()

    private val _tabType = MutableStateFlow(UserProfileTab.WRITTEN_POSTS)
    val tabType = _tabType.asStateFlow()

    val displayedPosts = tabType
        .flatMapLatest { tab ->
            postRepository.getUserPosts(providerId, tab)
        }
        .cachedIn(viewModelScope)

    init {
        getUserInfo()
    }

    private fun getUserInfo() = viewModelScope.launch {
        userRepository.loadUserInfo(providerId).onSuccess {
            _userInfo.value = it
        }.onFailure {
            errorHelper.logError(it)
        }
    }

    fun setTabType(tab: UserProfileTab) {
        _tabType.value = tab
        viewModelScope.launch {
            analyticsHelper.trackActionEvent(
                screenName = "user_profile",
                actionName = "select_tab",
                properties = mutableMapOf("tab_type" to tab.name)
            )
        }
    }

    sealed class UserProfileEvent {
        data class NavigateToPost(val postFeed: PostFeed) : UserProfileEvent()
        data object NavigateBack : UserProfileEvent()
    }
}

