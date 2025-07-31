package com.virtuous.network.source.user

import com.virtuous.network.model.user.LoadUserInfoResponse
import java.io.InputStream

interface UserDataSource {
    suspend fun loadMyUserInfo(): Result<LoadUserInfoResponse>
    suspend fun loadUserInfo(providerId : String): Result<LoadUserInfoResponse>
    suspend fun updateNickname(nickname: String): Result<LoadUserInfoResponse>
    suspend fun updateProfileImage(profileImage: InputStream?): Result<LoadUserInfoResponse>
}