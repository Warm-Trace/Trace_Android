package com.virtuous.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtuous.analytics.AnalyticsHelper
import com.virtuous.analytics.error.ErrorHelper
import com.virtuous.domain.repository.NotificationRepository
import com.virtuous.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val errorHelper: ErrorHelper,
) : ViewModel() {
    private val _eventChannel = Channel<SplashEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    init {
        checkSession()
    }

    fun checkSession() = viewModelScope.launch {
        userRepository.checkTokenHealth()
            .onSuccess { isExpired ->
                if (isExpired) _eventChannel.send(SplashEvent.NavigateToLogin)
                else _eventChannel.send(SplashEvent.NavigateToHome)
            }
            .onFailure {
                _eventChannel.send(SplashEvent.NavigateToLogin)
                errorHelper.logError(it)
            }
    }

    sealed class SplashEvent {
        data object NavigateToLogin : SplashEvent()
        data object NavigateToHome : SplashEvent()
    }
}