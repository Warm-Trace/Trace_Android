package com.virtuous.mypage.graph.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.virtuous.analytics.AnalyticsHelper
import com.virtuous.analytics.error.ErrorHelper
import com.virtuous.domain.model.mypage.MyPageTab
import com.virtuous.domain.model.post.PostFeed
import com.virtuous.domain.model.user.SwallowLevel
import com.virtuous.domain.model.user.UserInfo
import com.virtuous.domain.repository.PostRepository
import com.virtuous.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val analyticsHelper: AnalyticsHelper,
    private val errorHelper: ErrorHelper,
) : ViewModel() {
    private val _eventChannel = Channel<MyPageEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    internal fun onEvent(event: MyPageEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    private val _userInfo = MutableStateFlow(
        UserInfo(
            "닉네임", null, 0, 0, 0, 0
        )
    )
    val userInfo = _userInfo.asStateFlow()

    val swallowLevel: StateFlow<SwallowLevel> = _userInfo.map {
        SwallowLevel.getLevel(
            it.verifiedPostCount,
            it.completedMissionCount
        )
    }.distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SwallowLevel.getLevel(
                _userInfo.value.verifiedPostCount,
                _userInfo.value.completedMissionCount
            )
        )

    private
    val _tapType = MutableStateFlow(MyPageTab.WRITTEN_POSTS)
    val tabType = _tapType.asStateFlow()

    val displayedPosts = tabType
        .flatMapLatest { tab ->
            postRepository.getMyPosts(tab)
        }
        .cachedIn(viewModelScope)

    init {
        getUserInfo()
    }

    fun getUserInfo() = viewModelScope.launch {
        userRepository.getMyUserInfo().onSuccess { userInfo ->
            _userInfo.value = userInfo
        }.onFailure {
            errorHelper.logError(it)
        }
    }

    fun setTabType(tab: MyPageTab) {
        _tapType.value = tab
        viewModelScope.launch {
            analyticsHelper.trackActionEvent(
                screenName = "my_page",
                actionName = "select_tab",
                properties = mutableMapOf("tab_type" to tab.name)
            )
        }
    }

    sealed class MyPageEvent {
        data object NavigateToEditProfile : MyPageEvent()
        data class NavigateToPost(val postFeed: PostFeed) : MyPageEvent()
        data object NavigateToSetting : MyPageEvent()
        data object NavigateToSwallow : MyPageEvent()
    }
}




