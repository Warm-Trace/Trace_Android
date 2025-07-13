package com.virtuous.datastore.datasource.keyword

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named

class LocalKeywordDataSourceImpl @Inject constructor(
    @Named("keyword") private val dataStore : DataStore<Preferences>
) : LocalKeywordDataSource {
    override val recentKeywords: Flow<List<String>>  = dataStore.data
    .catch { exception ->
        if (exception is IOException)
            emit(emptyPreferences())
        else
            throw exception
    } .map { preferences ->
            preferences[RECENT_KEYWORD]
                ?.split(DELIMITER)
                ?.filter { it.isNotBlank() }
                ?: emptyList()
        }

    override suspend fun addKeyword(keyword: String) {
        dataStore.edit { preferences ->
            val current = preferences[RECENT_KEYWORD]
                ?.split(DELIMITER)
                ?.filter { it.isNotBlank() }
                ?.toMutableList() ?: mutableListOf()

            current.remove(keyword) // 중복 제거
            current.add(0, keyword) // 가장 최근 키워드 맨 앞에 추가

            preferences[RECENT_KEYWORD] = current.joinToString(DELIMITER)
        }
    }

    override suspend fun removeKeyword(keyword: String) {
        dataStore.edit { preferences ->
            val current = preferences[RECENT_KEYWORD]
                ?.split(DELIMITER)
                ?.filter { it.isNotBlank() && it != keyword }
                ?: return@edit

            preferences[RECENT_KEYWORD] = current.joinToString(DELIMITER)
        }
    }

    override suspend fun clearKeywords() {
        dataStore.edit { preferences ->
            preferences.remove(RECENT_KEYWORD)
        }
    }

    private companion object {
        private val RECENT_KEYWORD = stringPreferencesKey("RECENT_KEYWORD")
        private const val DELIMITER = "|"  // 저장할 때 사용한 구분자
    }
}