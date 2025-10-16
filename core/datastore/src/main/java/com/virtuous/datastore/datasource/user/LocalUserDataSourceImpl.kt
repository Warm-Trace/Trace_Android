package com.virtuous.datastore.datasource.user

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.virtuous.datastore.di.UserDataSource
import com.virtuous.domain.model.user.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException
import javax.inject.Inject
import kotlinx.serialization.json.Json

class LocalUserDataSourceImpl @Inject constructor(
    @UserDataSource private val datastore: DataStore<Preferences>,
) : LocalUserDataSource {
    override val userInfo: Flow<UserInfo?> = datastore.data
        .catch { exception ->
            if (exception is IOException)
                emit(emptyPreferences())
            else
                throw exception
        }.map { preferences ->
            val jsonString = preferences[USER_INFO]
            if (jsonString.isNullOrEmpty()) {
                null
            } else {
                try {
                    Json.decodeFromString<UserInfo>(jsonString)
                } catch (e: Exception) {
                    Log.e("LocalUserDataSourceImpl", "SerializationException: ${e.message}")
                    null
                }
            }
        }

    override suspend fun setUserInfo(userInfo: UserInfo) {
        datastore.edit { preferences ->
            try {
                preferences[USER_INFO] = Json.encodeToString(userInfo)
            } catch (e: SerializationException) {
                Log.e("LocalUserDataSourceImpl", "SerializationException: ${e.message}")
            }
        }
    }

    override suspend fun clearUserInfo() {
        datastore.edit { preferences ->
            preferences.remove(USER_INFO)
        }
    }

    companion object {
        private val USER_INFO = stringPreferencesKey("USER_INFO")
    }

}