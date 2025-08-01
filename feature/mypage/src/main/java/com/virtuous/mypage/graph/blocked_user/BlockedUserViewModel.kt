package com.virtuous.mypage.graph.blocked_user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtuous.domain.model.user.BlockedUser
import com.virtuous.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BlockedUserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _blockedUsers: MutableStateFlow<List<BlockedUser>> = MutableStateFlow(emptyList())
    val blockedUsers = _blockedUsers.asStateFlow()

    init {
        getBlockedUsers()
    }

    fun getBlockedUsers() = viewModelScope.launch {
        userRepository.getBlockedUsers().onSuccess {
            _blockedUsers.value = it
        }
    }
}