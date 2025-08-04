package com.virtuous.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.virtuous.domain.model.post.PostFeed
import com.virtuous.domain.model.search.SearchTab
import com.virtuous.domain.model.search.SearchType
import com.virtuous.network.model.cursor.Cursor
import com.virtuous.network.source.search.SearchDataSource

class SearchPagingSource(
    private val searchDataSource: SearchDataSource,
    private val keyword: String,
    private val tabType: SearchTab,
    private val searchType: SearchType,
    private val pageSize: Int = 10,
) : PagingSource<Cursor<Int>, PostFeed>() {

    override suspend fun load(params: LoadParams<Cursor<Int>>): LoadResult<Cursor<Int>, PostFeed> {
        return try {
            val cursor = params.key

            val response = searchDataSource.searchPosts(
                cursorDateTime = cursor?.dateTime,
                cursorId = cursor?.id,
                size = pageSize,
                keyword = keyword,
                tabType = tabType,
                searchType = searchType
            ).getOrThrow()

            val postFeeds = response.toDomain()

            val nextCursor = if (response.hasNext && response.cursor != null) Cursor<Int>(
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

    override fun getRefreshKey(state: PagingState<Cursor<Int>, PostFeed>): Cursor<Int>? = null
}