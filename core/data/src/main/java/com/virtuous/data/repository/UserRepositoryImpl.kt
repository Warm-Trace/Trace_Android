package com.virtuous.data.repository

import com.virtuous.common.util.suspendRunCatching
import com.virtuous.data.image.ImageResizer
import com.virtuous.datastore.datasource.token.LocalTokenDataSource
import com.virtuous.datastore.datasource.user.LocalUserDataSource
import com.virtuous.domain.model.user.BlockedUser
import com.virtuous.domain.model.user.UserInfo
import com.virtuous.domain.repository.UserRepository
import com.virtuous.network.source.auth.AuthDataSource
import com.virtuous.network.source.user.UserDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val localTokenDataSource: LocalTokenDataSource,
    private val authDataSource: AuthDataSource,
    private val userDataSource: UserDataSource,
    private val localUserDataSource: LocalUserDataSource,
    private val imageResizer: ImageResizer,
) : UserRepository {
    override suspend fun checkTokenHealth(): Result<Boolean> = suspendRunCatching {
        val token = localTokenDataSource.accessToken.first()
        if (token.isEmpty()) throw IllegalStateException("Access token is empty")

        authDataSource.checkTokenHealth(token).getOrElse {
            return@suspendRunCatching true
        }.isExpired
    }

    override suspend fun getMyUserInfo(): Result<UserInfo> = suspendRunCatching {
        val userInfo = localUserDataSource.userInfo.firstOrNull()
        userInfo ?: loadMyUserInfo().getOrThrow()
    }

    override suspend fun loadMyUserInfo(): Result<UserInfo> = suspendRunCatching {
        val response = userDataSource.loadMyUserInfo().getOrThrow()
        val userInfo = UserInfo(
            name = response.nickname,
            profileImageUrl = response.profileImageUrl,
            verificationScore = response.verificationScore,
            verificationCount = response.verificationCount,
        )

        localUserDataSource.setUserInfo(userInfo)
        userInfo
    }

    override suspend fun loadUserInfo(providerId: String): Result<UserInfo> = suspendRunCatching {
        val response = userDataSource.loadUserInfo(providerId).getOrThrow()
        UserInfo(
            name = response.nickname,
            profileImageUrl = response.profileImageUrl,
            verificationScore = response.verificationScore,
            verificationCount = response.verificationCount,
        )
    }

    override suspend fun updateNickname(nickname: String): Result<Unit> = suspendRunCatching {
        userDataSource.updateNickname(nickname)
    }

    override suspend fun updateProfileImage(profileImageUrl: String?): Result<Unit> =
        suspendRunCatching {
            val uploadImageUrl = profileImageUrl?.let { imageResizer.resizeImage(profileImageUrl) }
            userDataSource.updateProfileImage(uploadImageUrl)
        }

    override suspend fun getBlockedUsers(): Result<List<BlockedUser>> = suspendRunCatching {
        val response = userDataSource.getBlockedUsers().getOrThrow()
        response.toDomain()
    }

    override suspend fun unBlockUser(providerId: String): Result<Unit> = suspendRunCatching {
        userDataSource.unBlockUser(providerId)
    }
}