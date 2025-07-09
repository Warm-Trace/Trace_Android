package com.example.mypage.graph.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.domain.model.mypage.MyPageTab
import com.example.domain.model.post.PostFeed
import com.example.domain.model.user.UserInfo
import com.example.domain.repository.PostRepository
import com.example.domain.repository.UserRepository
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
class MyPageViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _eventChannel = Channel<MyPageEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    internal fun onEvent(event: MyPageEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    init {
        getUserInfo()
    }

    private val _userInfo = MutableStateFlow(
        UserInfo(
            "닉네임", null, 0, 0
        )
    )
    val userInfo = _userInfo.asStateFlow()

    private val _tapType = MutableStateFlow(MyPageTab.WRITTEN_POSTS)
    val tabType = _tapType.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val displayedPosts = tabType
        .flatMapLatest { tab ->
            postRepository.getMyPosts(tab)
        }
        .cachedIn(viewModelScope)

    fun getUserInfo() = viewModelScope.launch {
        userRepository.getUserInfo().onSuccess { userInfo ->
            _userInfo.value = userInfo
        }
    }

    fun setTabType(tab: MyPageTab) {
        _tapType.value = tab
    }

    sealed class MyPageEvent {
        data object NavigateToEditProfile : MyPageEvent()
        data class NavigateToPost(val postFeed: PostFeed) : MyPageEvent()
        data object NavigateToSetting : MyPageEvent()
    }
}




