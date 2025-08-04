package com.virtuous.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.virtuous.common.util.suspendRunCatching
import com.virtuous.data.paging.NotificationPagingSource
import com.virtuous.domain.model.notification.Notification
import com.virtuous.domain.repository.NotificationRepository
import com.virtuous.network.source.notification.NotificationDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationDataSource: NotificationDataSource
) : NotificationRepository {
    override suspend fun updateDeviceToken(token: String): Result<Unit> = suspendRunCatching {
        notificationDataSource.updateDeviceToken(token)
    }

    override fun getNotifications(): Flow<PagingData<Notification>> {
        return Pager(
            config = PagingConfig(pageSize = DEFAULT_PAGE_SIZE),
            pagingSourceFactory = {
                NotificationPagingSource(notificationDataSource)
            }
        ).flow
    }

    override suspend fun readNotification(notificationId: String): Result<Unit> =
        suspendRunCatching {
            notificationDataSource.readNotification(notificationId)
        }

    override suspend fun deleteNotification(notificationId: String): Result<Unit> =
        suspendRunCatching {
            notificationDataSource.deleteNotification(notificationId)
        }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }
}