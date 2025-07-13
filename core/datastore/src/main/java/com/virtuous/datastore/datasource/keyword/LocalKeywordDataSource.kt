package com.virtuous.datastore.datasource.keyword

import kotlinx.coroutines.flow.Flow

interface LocalKeywordDataSource {
    val recentKeywords : Flow<List<String>>
    suspend fun addKeyword(keyword : String)
    suspend fun removeKeyword(keyword : String)
    suspend fun clearKeywords()
}