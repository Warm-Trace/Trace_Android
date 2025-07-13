package com.virtuous.network.source.notification

import com.google.android.gms.tasks.Tasks
import com.google.firebase.messaging.FirebaseMessaging
import com.virtuous.network.api.TraceApi
import com.virtuous.network.model.notification.PostDeviceTokenRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
}