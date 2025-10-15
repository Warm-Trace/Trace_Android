package com.virtuous.auth.graph.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtuous.domain.model.auth.UserRole
import com.virtuous.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _eventChannel = Channel<LoginEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    internal fun loginKakao(idToken: String) = viewModelScope.launch {
        authRepository.loginKakao(idToken).onSuccess { user ->
            if (user.role == UserRole.NONE) {
                val signUpToken = user.signUpToken
                val providerId = user.providerId

                if (signUpToken != null && providerId != null) {
                    _eventChannel.send(LoginEvent.NavigateToEditProfile(signUpToken, providerId))
                } else {
                    _eventChannel.send(LoginEvent.ShowSnackbar("필수 토큰 정보가 누락되었습니다"))
                }
            } else {
                _eventChannel.send(LoginEvent.NavigateToHome)
            }
        }.onFailure {
            _eventChannel.send(LoginEvent.ShowSnackbar("로그인에 실패했습니다"))
        }
    }

    sealed class LoginEvent {
        data object NavigateToHome : LoginEvent()
        data class NavigateToEditProfile(val signUpToken: String, val providerId: String) : LoginEvent()
        data class ShowSnackbar(val message: String) : LoginEvent()
    }

}