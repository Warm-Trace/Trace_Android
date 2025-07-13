package com.virtuous.network.source.search

import com.virtuous.domain.model.search.SearchTab
import com.virtuous.domain.model.search.SearchType
import com.virtuous.network.model.post.GetPostsResponse
import kotlinx.datetime.LocalDateTime

interface SearchDataSource {
    suspend fun searchPosts(
        cursorDateTime: LocalDateTime?,
        cursorId: Int?,
        size: Int,
        tabType: SearchTab,
        keyword: String,
        searchType: SearchType
    ): Result<GetPostsResponse>
}