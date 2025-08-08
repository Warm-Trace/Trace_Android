package com.virtuous.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.virtuous.domain.model.notification.Notification
import com.virtuous.network.model.cursor.Cursor
import com.virtuous.network.source.notification.NotificationDataSource

class NotificationPagingSource(
    private val notificationDataSource: NotificationDataSource,
    private val pageSize: Int = 20,
) : PagingSource<Cursor<String>, Notification>() {

    override suspend fun load(params: LoadParams<Cursor<String>>): LoadResult<Cursor<String>, Notification> {
        return try {
            val cursor = params.key

            val response = notificationDataSource.getNotifications(
                cursorDateTime = cursor?.dateTime,
                cursorId = cursor?.id,
                size = pageSize,
            ).getOrThrow()

            val notifications = response.toDomain()

            val nextCursor = if (response.hasNext && response.cursor != null) Cursor<String>(
                id = response.cursor?.id
                    ?: throw IllegalStateException("Cursor must be present when hasNext is true"),
                dateTime = response.cursor?.dateTime
                    ?: throw IllegalStateException("Cursor must be present when hasNext is true")

            ) else null

            val safeNextCursor = if (nextCursor == params.key) null else nextCursor

            LoadResult.Page(
                data = notifications,
                prevKey = null,
                nextKey = safeNextCursor
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Cursor<String>, Notification>): Cursor<String>? = null
}