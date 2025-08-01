package com.virtuous.home.graph.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.virtuous.domain.model.notification.Notification
import com.virtuous.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {
    val notifications: Flow<PagingData<Notification>> = flow {
        emitAll(notificationRepository.getNotifications())
    }.cachedIn(viewModelScope)

    fun readNotification(notificationId: Int) = viewModelScope.launch {
        notificationRepository.readNotification(notificationId)
    }

    fun deleteNotification(notificationId: Int) = viewModelScope.launch {
        notificationRepository.deleteNotification(notificationId)
    }
}
