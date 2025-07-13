package com.virtuous.network.source.notification

interface NotificationDataSource {
    suspend fun updateDeviceToken(token : String) : Result<Unit>
    suspend fun postDeviceToken() : Result<Unit>
    suspend fun getDeviceToken() : String
}