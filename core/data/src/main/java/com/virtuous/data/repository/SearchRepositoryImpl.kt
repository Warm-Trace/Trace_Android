package com.virtuous.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.virtuous.common.util.suspendRunCatching
import com.virtuous.data.paging.SearchPagingSource
import com.virtuous.datastore.datasource.keyword.LocalKeywordDataSource
import com.virtuous.domain.model.post.PostFeed
import com.virtuous.domain.model.search.SearchType
import com.virtuous.domain.model.search.SearchTab
import com.virtuous.domain.repository.SearchRepository
import com.virtuous.network.source.search.SearchDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val searchDataSource: SearchDataSource,
    private val localKeywordDataSource: LocalKeywordDataSource,
) : SearchRepository {
    override suspend fun searchPosts(
        keyword: String,
        tabType: SearchTab,
        searchType: SearchType
    ): Flow<PagingData<PostFeed>> {
        return Pager(
            config = PagingConfig(pageSize = DEFAULT_PAGE_SIZE),
            pagingSourceFactory = {
                SearchPagingSource(searchDataSource, keyword, tabType, searchType)
            }
        ).flow
    }

    override suspend fun getRecentKeywords(): Result<List<String>> = suspendRunCatching {
        localKeywordDataSource.recentKeywords.firstOrNull() ?: emptyList()
    }

    override suspend fun addKeyword(keyword: String): Result<Unit> = suspendRunCatching {
        localKeywordDataSource.addKeyword(keyword)
    }

    override suspend fun removeKeyword(keyword: String): Result<Unit> = suspendRunCatching {
        localKeywordDataSource.removeKeyword(keyword)
    }

    override suspend fun clearKeywords(): Result<Unit> = suspendRunCatching {
        localKeywordDataSource.clearKeywords()
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }
}