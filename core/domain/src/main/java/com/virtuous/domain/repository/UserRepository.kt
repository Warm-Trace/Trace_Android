package com.virtuous.domain.repository

import com.virtuous.domain.model.user.UserInfo

interface UserRepository {
    suspend fun checkTokenHealth(): Result<Boolean>
    suspend fun getMyUserInfo(): Result<UserInfo>
    suspend fun loadMyUserInfo(): Result<UserInfo>
    suspend fun loadUserInfo(providerId : String) : Result<UserInfo>
    suspend fun updateNickname(nickname: String): Result<Unit>
    suspend fun updateProfileImage(profileImageUrl: String?): Result<Unit>
}