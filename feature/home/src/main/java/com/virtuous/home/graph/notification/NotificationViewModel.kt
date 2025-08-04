package com.virtuous.home.graph.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.virtuous.domain.model.notification.Notification
import com.virtuous.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
) : ViewModel() {
    val notifications: Flow<PagingData<Notification>> = flow {
        emitAll(
            notificationRepository.getNotifications()
                .combine(_deletedNotificationIds) { pagingData, deletedIds ->
                    pagingData.filter { notification ->
                        notification.id !in deletedIds
                    }
                }
        )
    }.cachedIn(viewModelScope)

    private val _deletedNotificationIds = MutableStateFlow<Set<String>>(emptySet())

    fun readNotification(notificationId: String) = viewModelScope.launch {
        notificationRepository.readNotification(notificationId)
    }

    fun deleteNotification(notificationId: String) = viewModelScope.launch {
        notificationRepository.deleteNotification(notificationId)
        _deletedNotificationIds.value += notificationId
    }
}
