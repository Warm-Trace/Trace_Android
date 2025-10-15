package com.virtuous.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtuous.domain.repository.NotificationRepository
import com.virtuous.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    suspend fun checkSession(): Boolean  {
        return userRepository.checkTokenHealth().fold(
            onSuccess = { isExpired -> !isExpired },
            onFailure = { false }
        )
    }
    fun readNotification(notificationId : String) = viewModelScope.launch {
        notificationRepository.readNotification(notificationId)
    }
}