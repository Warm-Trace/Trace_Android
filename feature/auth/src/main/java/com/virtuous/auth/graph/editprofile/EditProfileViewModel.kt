package com.virtuous.auth.graph.editprofile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtuous.common_ui.event.EventHelper
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
    val eventHelper: EventHelper,
    private val savedStateHandle: SavedStateHandle,
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
    val profileImage = _profileImageUrl.asStateFlow()

    fun setName(name: String) {
        _name.value = name
    }

    fun setProfileImageUrl(imageUrl: String?) {
        _profileImageUrl.value = imageUrl
    }

    internal fun registerUser() = viewModelScope.launch {
        authRepository.registerUser(signUpToken, providerId, name.value, profileImage.value)
            .onSuccess {
               _eventChannel.send(EditProfileEvent.RegisterUserSuccess)
            }.onFailure {
            _eventChannel.send(EditProfileEvent.RegisterUserFailure)
        }
    }


    sealed class EditProfileEvent {
        data object RegisterUserSuccess : EditProfileEvent()
        data object RegisterUserFailure : EditProfileEvent()
    }
}