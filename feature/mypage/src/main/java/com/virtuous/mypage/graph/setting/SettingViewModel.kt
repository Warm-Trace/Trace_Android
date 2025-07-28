package com.virtuous.mypage.graph.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtuous.common_ui.event.EventHelper
import com.virtuous.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    val eventHelper: com.virtuous.common_ui.event.EventHelper,
) : ViewModel() {
    private val _eventChannel = Channel<SettingEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    fun logout() = viewModelScope.launch {
        authRepository.logOut()
        _eventChannel.send(SettingEvent.Logout)
    }

    fun unregisterUser() = viewModelScope.launch {
        authRepository.unregisterUser().onSuccess {
            _eventChannel.send(SettingEvent.UnregisterUserSuccess)
        }.onFailure {
            _eventChannel.send(SettingEvent.UnregisterUserFailure)
        }
    }

    sealed class SettingEvent {
        data object NavigateBack : SettingEvent()
        data object Logout : SettingEvent()
        data object UnregisterUserSuccess : SettingEvent()
        data object UnregisterUserFailure : SettingEvent()
    }
}
