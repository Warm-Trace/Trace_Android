package com.virtuous.auth.graph.editprofile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtuous.analytics.AnalyticsHelper
import com.virtuous.analytics.error.ErrorHelper
import com.virtuous.domain.model.user.NameRule
import com.virtuous.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val savedStateHandle: SavedStateHandle,
    private val analyticsHelper: AnalyticsHelper,
    private val errorHelper: ErrorHelper,
) : ViewModel() {
    private val _eventChannel = Channel<EditProfileEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    private val signUpToken: String = requireNotNull(savedStateHandle["signUpToken"])
    private val providerId: String = requireNotNull(savedStateHandle["providerId"])

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    val isNameValid: StateFlow<Boolean> = name.map {
        it.trim().length in NameRule.NAME_MIN_LENGTH..NameRule.NAME_MAX_LENGTH
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false,
    )

    private val _profileImageUrl = MutableStateFlow<String?>(null)
    val profileImageUrl = _profileImageUrl.asStateFlow()

    fun setName(name: String) {
        _name.value = name
    }

    fun setProfileImageUrl(imageUrl: String?) {
        _profileImageUrl.value = imageUrl
    }

    internal fun registerUser() = viewModelScope.launch {
        authRepository.registerUser(signUpToken, providerId, _name.value, _profileImageUrl.value)
            .onSuccess {
                _eventChannel.send(EditProfileEvent.NavigateToHome)
                analyticsHelper.trackActionEvent(
                    screenName = "edit_profile",
                    actionName = "register_user",
                    properties = mutableMapOf(
                        "name" to name.value,
                        "has_profile_image" to (_profileImageUrl.value != null),
                    )
                )
            }.onFailure {
                _eventChannel.send(EditProfileEvent.ShowSnackbar("프로필 설정에 실패했습니다"))
                errorHelper.logError(it)
            }
    }

    sealed class EditProfileEvent {
        data object NavigateToHome : EditProfileEvent()
        data class ShowSnackbar(val message: String) : EditProfileEvent()
    }
}