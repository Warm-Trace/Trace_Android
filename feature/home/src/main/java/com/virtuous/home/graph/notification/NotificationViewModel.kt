package com.virtuous.home.graph.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.virtuous.analytics.AnalyticsHelper
import com.virtuous.domain.model.notification.Notification
import com.virtuous.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val analyticsHelper: AnalyticsHelper,
) : ViewModel() {
    private val _deletedNotificationIds = MutableStateFlow<Set<String>>(emptySet())
    private val _readNotificationIds = MutableStateFlow<Set<String>>(emptySet())

    private val _cachedNotifications: Flow<PagingData<Notification>> by lazy {
        notificationRepository.getNotifications().cachedIn(viewModelScope)
    }

    val notifications: Flow<PagingData<Notification>> =
        combine(
            _cachedNotifications,
            _deletedNotificationIds,
            _readNotificationIds
        ) { pagingData, deletedIds, readIds ->
            pagingData.filter {
                it.id !in deletedIds
            }.map {
                if (it.id in readIds) {
                    it.copy(isRead = true)
                } else {
                    it
                }
            }
        }

    fun readNotification(notificationId: String) = viewModelScope.launch {
        notificationRepository.readNotification(notificationId)
        _readNotificationIds.value += notificationId
        analyticsHelper.trackActionEvent(
            screenName = "notification",
            actionName = "read_notification",
        )
    }

    fun deleteNotification(notificationId: String) = viewModelScope.launch {
        notificationRepository.deleteNotification(notificationId)
        _deletedNotificationIds.value += notificationId
        analyticsHelper.trackActionEvent(
            screenName = "notification",
            actionName = "delete_notification",
        )
    }

    fun onRefresh() {
        _deletedNotificationIds.value = emptySet()
        _readNotificationIds.value = emptySet()
        viewModelScope.launch {
            analyticsHelper.trackActionEvent(
                screenName = "notification",
                actionName = "refresh_notification",
            )
        }
    }
}
