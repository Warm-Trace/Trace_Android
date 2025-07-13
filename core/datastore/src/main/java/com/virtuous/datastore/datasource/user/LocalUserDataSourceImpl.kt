package com.virtuous.datastore.datasource.user

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.virtuous.datastore.util.fromJsonOrNull
import com.virtuous.domain.model.user.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named

class LocalUserDataSourceImpl @Inject constructor(
    @Named("user") private val datastore : DataStore<Preferences>,
    private val gson : Gson,
) : LocalUserDataSource {
    override val userInfo: Flow<UserInfo?> = datastore.data
        .catch { exception ->
            if (exception is IOException)
                emit(emptyPreferences())
            else
                throw exception
        }.map { preferences ->
            val userInfo = preferences[USER_INFO] ?: ""
            gson.fromJsonOrNull(userInfo)
        }

    override suspend fun setUserInfo(userInfo: UserInfo) {
        val jsonString = gson.toJson(userInfo)
        datastore.edit { preferences ->
            preferences[USER_INFO] = jsonString
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