package com.virtuous.network.source.notification

import com.google.android.gms.tasks.Tasks
import com.google.firebase.messaging.FirebaseMessaging
import com.virtuous.network.api.TraceApi
import com.virtuous.network.model.notification.GetNotificationsResponse
import com.virtuous.network.model.notification.PostDeviceTokenRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject

class NotificationDataSourceImpl @Inject constructor(
    private val traceApi: TraceApi,
    private val firebaseMessaging: FirebaseMessaging,
) : NotificationDataSource {
    override suspend fun updateDeviceToken(token: String): Result<Unit> =
        traceApi.postDeviceToken(postDeviceTokenRequest = PostDeviceTokenRequest(token))

    override suspend fun postDeviceToken(): Result<Unit> {
        val token = getDeviceToken()
        return traceApi.postDeviceToken(postDeviceTokenRequest = PostDeviceTokenRequest(token))
    }

    override suspend fun getDeviceToken(): String = withContext(Dispatchers.IO) {
        try {
            Tasks.await(firebaseMessaging.token)
        } catch (e: Exception) {
            throw Exception("Failed to get FCM token", e)
        }
    }

    override suspend fun getNotifications(
        cursorDateTime: LocalDateTime?,
        cursorId: Int?,
        size: Int
    ): Result<GetNotificationsResponse> = traceApi.getNotifications(
        cursorDateTime = cursorDateTime,
        cursorId = cursorId,
        size = size
    )

    override suspend fun readNotification(notificationId: String): Result<Unit> =
        traceApi.readNotification(notificationId)

    override suspend fun deleteNotification(notificationId: String): Result<Unit> =
        traceApi.deleteNotification(id = notificationId)
}