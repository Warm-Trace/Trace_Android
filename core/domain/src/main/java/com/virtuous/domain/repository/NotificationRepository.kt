package com.virtuous.domain.repository

interface NotificationRepository {
    suspend fun updateDeviceToken(token : String) : Result<Unit>
}