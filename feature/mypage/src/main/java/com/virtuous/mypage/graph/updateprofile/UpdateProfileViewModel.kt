package com.virtuous.mypage.graph.updateprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtuous.common_ui.event.EventHelper
import com.virtuous.common_ui.event.TraceEvent
import com.virtuous.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val eventHelper: com.virtuous.common_ui.event.EventHelper
) : ViewModel() {
    private val _eventChannel = Channel<UpdateProfileEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _isNameValid = MutableStateFlow(true)
    val isNameValid = _isNameValid.asStateFlow()

    private val _profileImageUrl = MutableStateFlow<String?>(null)
    val profileImage = _profileImageUrl.asStateFlow()

    private val _isNameChanged = MutableStateFlow(false)
    val isNameChanged = _isNameChanged.asStateFlow()

    private val _isProfileImageChanged = MutableStateFlow(false)
    val isProfileImageChanged = _isProfileImageChanged.asStateFlow()

    private var originalName: String = ""
    private var originalProfileImageUrl: String? = null

    init {
        getUserInfo()
    }

    private fun getUserInfo() = viewModelScope.launch {
        userRepository.loadUserInfo().onSuccess { userInfo ->
            originalName = userInfo.name
            originalProfileImageUrl = userInfo.profileImageUrl

            setName(userInfo.name)
            setProfileImageUrl(userInfo.profileImageUrl)
        }
    }

    fun setName(name: String) {
        _name.value = name
        validateName()
        _isNameChanged.value = _name.value != originalName
    }

    fun setProfileImageUrl(imageUrl: String?) {
        _profileImageUrl.value = imageUrl
        _isProfileImageChanged.value = _profileImageUrl.value != originalProfileImageUrl
    }

    private fun validateName() {
        val nicknameRegex = "^.{$NAME_MIN_LENGTH,$NAME_MAX_LENGTH}$".toRegex()
        _isNameValid.value = _name.value.trim().matches(nicknameRegex)
    }

    fun updateProfile() {
        if (!_isNameChanged.value && !_isProfileImageChanged.value) return

        viewModelScope.launch {
            var success = true

            if (_isNameChanged.value) {
                val result = userRepository.updateNickname(_name.value)
                if (result.isFailure) success = false
            }

            if (_isProfileImageChanged.value) {
                val result = userRepository.updateProfileImage(_profileImageUrl.value)
                if (result.isFailure) success = false
            }

            if (success) {
                userRepository.loadUserInfo().onSuccess {
                    _eventChannel.send(UpdateProfileEvent.NavigateBack)
                }
            } else {
                eventHelper.sendEvent(com.virtuous.common_ui.event.TraceEvent.ShowSnackBar("프로필 수정에 실패했습니다."))
            }
        }
    }

    companion object {
        private const val NAME_MIN_LENGTH = 2
        private const val NAME_MAX_LENGTH = 12
    }

    sealed class UpdateProfileEvent {
        data object NavigateBack : UpdateProfileEvent()
    }
}
