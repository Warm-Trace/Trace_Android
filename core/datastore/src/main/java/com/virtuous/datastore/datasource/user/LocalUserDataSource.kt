package com.virtuous.datastore.datasource.user

import com.virtuous.domain.model.user.UserInfo
import kotlinx.coroutines.flow.Flow

interface LocalUserDataSource {
    val userInfo : Flow<UserInfo?>
    suspend fun setUserInfo(userInfo: UserInfo)
    suspend fun clearUserInfo()
}