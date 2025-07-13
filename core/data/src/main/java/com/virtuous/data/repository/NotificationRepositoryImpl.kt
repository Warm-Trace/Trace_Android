package com.virtuous.data.repository

import com.virtuous.common.util.suspendRunCatching
import com.virtuous.domain.repository.NotificationRepository
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(

) : NotificationRepository {
    override suspend fun updateDeviceToken(token: String): Result<Unit> = suspendRunCatching {
        Result.success(Unit)
    }
}