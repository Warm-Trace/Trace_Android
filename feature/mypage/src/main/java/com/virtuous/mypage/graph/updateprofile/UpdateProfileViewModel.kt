package com.virtuous.mypage.graph.updateprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtuous.analytics.AnalyticsHelper
import com.virtuous.analytics.error.ErrorHelper
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
    private val analyticsHelper: AnalyticsHelper,
    private val errorHelper: ErrorHelper,
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
        getMyUserInfo()
    }

    private fun getMyUserInfo() = viewModelScope.launch {
        userRepository.loadMyUserInfo().onSuccess { userInfo ->
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
                userRepository.loadMyUserInfo()
                _eventChannel.send(UpdateProfileEvent.NavigateBack)
                analyticsHelper.trackActionEvent(
                    screenName = "update_profile",
                    actionName = "update_profile",
                    properties = mutableMapOf(
                        "is_name_changed" to _isNameChanged.value,
                        "is_profile_image_changed" to _isProfileImageChanged.value
                    )
                )
            } else {
                _eventChannel.send(UpdateProfileEvent.ShowSnackbar("프로필 수정에 실패했습니다."))
            }
        }
    }

    companion object {
        private const val NAME_MIN_LENGTH = 2
        private const val NAME_MAX_LENGTH = 12
    }

    sealed class UpdateProfileEvent {
        data object NavigateBack : UpdateProfileEvent()
        data class ShowSnackbar(val message: String) : UpdateProfileEvent()
    }
}
