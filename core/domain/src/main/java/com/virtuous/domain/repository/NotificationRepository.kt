package com.virtuous.domain.repository

import androidx.paging.PagingData
import com.virtuous.domain.model.notification.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun updateDeviceToken(token: String): Result<Unit>
    suspend fun getNotifications(): Flow<PagingData<Notification>>
}