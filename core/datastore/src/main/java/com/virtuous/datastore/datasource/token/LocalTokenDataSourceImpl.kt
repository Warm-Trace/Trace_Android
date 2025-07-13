package com.virtuous.datastore.datasource.token

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class LocalTokenDataSourceImpl @Inject constructor(
    @Named("token") private val dataStore: DataStore<Preferences>
) : LocalTokenDataSource {

    override val accessToken: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException)
                emit(emptyPreferences())
            else
                throw exception
        }.map { preferences ->
            preferences[ACCESS_TOKEN] ?: ""
        }


    override val refreshToken: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException)
                emit(emptyPreferences())
            else
                throw exception
        }.map { preferences ->
            preferences[REFRESH_TOKEN] ?: ""
        }

    override suspend fun setAccessToken(accessToken: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
        }
    }

    override suspend fun setRefreshToken(refreshToken: String) {
        dataStore.edit { preferences ->
            preferences[REFRESH_TOKEN] = refreshToken
        }
    }

    override suspend fun clearToken() {
        coroutineScope {
            launch {
                dataStore.edit { preferences ->
                    preferences.remove(REFRESH_TOKEN)
                }
            }

            launch {
                dataStore.edit { preferences ->
                    preferences.remove(ACCESS_TOKEN)
                }
            }
        }
    }

    private companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("ACCESS_TOKEN")
        private val REFRESH_TOKEN = stringPreferencesKey("REFRESH_TOKEN")
    }

}