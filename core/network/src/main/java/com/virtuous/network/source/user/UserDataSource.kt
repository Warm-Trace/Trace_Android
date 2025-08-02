package com.virtuous.network.source.user

import com.virtuous.network.model.user.GetBlockedUsersResponse
import com.virtuous.network.model.user.LoadUserInfoResponse
import java.io.InputStream

interface UserDataSource {
    suspend fun loadMyUserInfo(): Result<LoadUserInfoResponse>
    suspend fun loadUserInfo(providerId : String): Result<LoadUserInfoResponse>
    suspend fun updateNickname(nickname: String): Result<LoadUserInfoResponse>
    suspend fun updateProfileImage(profileImage: InputStream?): Result<LoadUserInfoResponse>
    suspend fun getBlockedUsers() : Result<GetBlockedUsersResponse>
    suspend fun unblockUser(providerId: String) : Result<Unit>
}