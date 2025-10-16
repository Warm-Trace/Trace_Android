package com.virtuous.mypage.graph.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtuous.analytics.AnalyticsHelper
import com.virtuous.analytics.error.ErrorHelper
import com.virtuous.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val analyticsHelper: AnalyticsHelper,
    private val errorHelper: ErrorHelper,
) : ViewModel() {
    private val _eventChannel = Channel<SettingEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    fun logout() = viewModelScope.launch {
        authRepository.logOut().onSuccess {
            analyticsHelper.trackActionEvent(
                screenName = "setting",
                actionName = "sign_out",
            )
        }.onFailure {
            errorHelper.logError(it)
        }

        _eventChannel.send(SettingEvent.NavigateToLogin)
    }

    fun unregisterUser() = viewModelScope.launch {
        authRepository.unregisterUser().onSuccess {
            _eventChannel.send(SettingEvent.NavigateToLogin)
            analyticsHelper.trackActionEvent(
                screenName = "setting",
                actionName = "withdrawal",
            )
        }.onFailure {
            _eventChannel.send(SettingEvent.ShowSnackbar("회원 탈퇴에 실패했습니다."))
            errorHelper.logError(it)
        }
    }

    sealed class SettingEvent {
        data object NavigateToLogin : SettingEvent()
        data class ShowSnackbar(val message: String) : SettingEvent()
    }
}
