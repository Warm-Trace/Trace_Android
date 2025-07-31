package com.virtuous.data.repository

import androidx.paging.PagingData
import com.virtuous.common.util.suspendRunCatching
import com.virtuous.domain.model.notification.Notification
import com.virtuous.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(

) : NotificationRepository {
    override suspend fun updateDeviceToken(token: String): Result<Unit> = suspendRunCatching {
        Result.success(Unit)
    }

    override suspend fun getNotifications(): Flow<PagingData<Notification>> {
        TODO("Not yet implemented")
    }

//    override suspend fun getNotifications(): Flow<PagingData<Notification>> {
//
//    }
}