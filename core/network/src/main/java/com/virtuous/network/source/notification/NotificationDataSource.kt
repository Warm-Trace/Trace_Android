package com.virtuous.network.source.notification

import com.virtuous.network.model.notification.GetNotificationsResponse
import kotlinx.datetime.LocalDateTime

interface NotificationDataSource {
    suspend fun updateDeviceToken(token: String): Result<Unit>
    suspend fun postDeviceToken(): Result<Unit>
    suspend fun getDeviceToken(): String
    suspend fun getNotifications(
        cursorDateTime: LocalDateTime?,
        cursorId: String?,
        size: Int,
    ): Result<GetNotificationsResponse>

    suspend fun readNotification(notificationId: String): Result<Unit>
    suspend fun deleteNotification(notificationId: String): Result<Unit>
}