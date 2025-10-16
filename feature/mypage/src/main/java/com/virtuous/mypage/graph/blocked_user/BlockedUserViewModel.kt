package com.virtuous.mypage.graph.blocked_user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtuous.analytics.AnalyticsHelper
import com.virtuous.analytics.error.ErrorHelper
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
    private val analyticsHelper: AnalyticsHelper,
    private val errorHelper: ErrorHelper,
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
        }.onFailure {
            errorHelper.logError(it)
        }
    }

    fun unblockUser(providerId: String) = viewModelScope.launch {
        userRepository.unblockUser(providerId).onSuccess {
            _eventChannel.send(BlockedUserEvent.ShowSnackbar("차단이 해제되었습니다."))
            analyticsHelper.trackActionEvent(
                screenName = "blocked_user",
                actionName = "unblock_user",
            )
        }.onFailure {
            _eventChannel.send(BlockedUserEvent.ShowSnackbar("차단 해제에 실패했습니다."))
            errorHelper.logError(it)
        }
    }


    sealed class BlockedUserEvent {
        data class ShowSnackbar(val message: String) : BlockedUserEvent()
    }
}