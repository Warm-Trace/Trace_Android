package com.example.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.domain.model.mission.MissionFeed
import com.example.network.model.cursor.Cursor
import com.example.network.source.mission.MissionDataSource

class MissionPagingSource(
    private val missionDataSource: MissionDataSource,
    private val pageSize: Int = 20
) : PagingSource<Cursor, MissionFeed>() {

    override suspend fun load(params: LoadParams<Cursor>): LoadResult<Cursor, MissionFeed> {
        return try {
            val cursor = params.key

            val response = missionDataSource.getCompletedMissions(
                cursorDateTime = cursor?.dateTime,
                cursorId = cursor?.id,
                size = pageSize,
            ).getOrThrow()

            val postFeeds = response.toDomain()

            val nextCursor = if (response.hasNext && response.cursor != null) Cursor(
                id = response.cursor?.id
                    ?: throw IllegalStateException("Cursor must be present when hasNext is true"),
                dateTime = response.cursor?.dateTime
                    ?: throw IllegalStateException("Cursor must be present when hasNext is true")

            ) else null

            val safeNextCursor = if (nextCursor == params.key) null else nextCursor

            LoadResult.Page(
                data = postFeeds,
                prevKey = null,
                nextKey = safeNextCursor
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Cursor, MissionFeed>): Cursor? = null
}