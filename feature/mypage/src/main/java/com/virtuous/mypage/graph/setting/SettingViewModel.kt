package com.virtuous.mypage.graph.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtuous.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _eventChannel = Channel<SettingEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    fun logout() = viewModelScope.launch {
        authRepository.logOut()
        _eventChannel.send(SettingEvent.Logout)
    }

    fun unregisterUser() = viewModelScope.launch {
        authRepository.unregisterUser().onSuccess {
            _eventChannel.send(SettingEvent.NavigateToLogin)
        }.onFailure {
            _eventChannel.send(SettingEvent.ShowSnackbar("회원 탈퇴에 실패했습니다."))
        }
    }

    sealed class SettingEvent {
        data object Logout : SettingEvent()
        data object NavigateToLogin : SettingEvent()
        data class ShowSnackbar(val message: String) : SettingEvent()
    }
}
