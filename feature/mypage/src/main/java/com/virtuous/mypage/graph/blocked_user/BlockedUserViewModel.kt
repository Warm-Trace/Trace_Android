package com.virtuous.mypage.graph.blocked_user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtuous.common_ui.event.EventHelper
import com.virtuous.domain.model.user.BlockedUser
import com.virtuous.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BlockedUserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    internal val eventHelper: EventHelper
) : ViewModel() {
    private val _eventChannel = Channel<BlockedUserEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    private val _blockedUsers: MutableStateFlow<List<BlockedUser>> = MutableStateFlow(emptyList())
    val blockedUsers = _blockedUsers.asStateFlow()

    init {
        getBlockedUsers()
    }

    private fun getBlockedUsers() = viewModelScope.launch {
        userRepository.getBlockedUsers().onSuccess {
            _blockedUsers.value = it
        }
    }

    fun unblockUser(providerId : String) = viewModelScope.launch {
        userRepository.unblockUser(providerId).onSuccess {
            _eventChannel.send(BlockedUserEvent.UnblockUserSuccess)
        }.onFailure {
            _eventChannel.send(BlockedUserEvent.UnblockUserFailure)
        }
    }

    sealed class BlockedUserEvent {
        data object UnblockUserSuccess : BlockedUserEvent()
        data object UnblockUserFailure : BlockedUserEvent()
    }
}