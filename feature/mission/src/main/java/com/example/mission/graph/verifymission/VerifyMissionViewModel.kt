package com.example.mission.graph.verifymission

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.event.EventHelper
import com.example.domain.repository.MissionRepository
import com.example.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyMissionViewModel @Inject constructor(
    private val missionRepository: MissionRepository,
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle,
    val eventHelper: EventHelper
) : ViewModel() {
    private val _eventChannel = Channel<VerifyMissionEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    val description: String = requireNotNull(savedStateHandle["description"])

    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _content = MutableStateFlow("")
    val content = _content.asStateFlow()

    private val _images: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val images = _images.asStateFlow()

    private val _isVerifyingMission = MutableStateFlow(false)
    val isVerifyingMission = _isVerifyingMission.asStateFlow()

    fun setTitle(title: String) {
        _title.value = title
    }

    fun setContent(content: String) {
        _content.value = content
    }

    fun addImages(images: List<String>) {
        _images.value += images
    }

    fun removeImage(image: String) {
        _images.value = _images.value.filter { it != image }
    }

    fun verifyMission() = viewModelScope.launch {
        _isVerifyingMission.value = true

        missionRepository.verifyDailyMission(
            title = _title.value,
            content = _content.value,
            images = _images.value
        ).onSuccess { postId ->
            _eventChannel.send(VerifyMissionEvent.VerifyMissionSuccess(postId = postId))
            userRepository.loadUserInfo()
        }.onFailure {
            _eventChannel.send(VerifyMissionEvent.VerifyMissionFailure)
        }

        _isVerifyingMission.value = false
    }

    sealed class VerifyMissionEvent {
        data object NavigateToBack : VerifyMissionEvent()
        data class VerifyMissionSuccess(val postId: Int) : VerifyMissionEvent()
        data object VerifyMissionFailure : VerifyMissionEvent()
    }
}