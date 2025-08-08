package com.virtuous.mypage.graph.swallow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtuous.domain.model.user.SwallowLevel
import com.virtuous.domain.model.user.UserInfo
import com.virtuous.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SwallowViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
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
    }.distinctUntilChanged().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SwallowLevel.getLevel(
            _userInfo.value.verifiedPostCount,
            _userInfo.value.completedMissionCount
        )
    )

    init {
        getUserInfo()
    }

    private fun getUserInfo() = viewModelScope.launch {
        userRepository.getMyUserInfo().onSuccess { userInfo ->
            _userInfo.value = userInfo
        }
    }
}