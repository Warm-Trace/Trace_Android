package com.virtuous.data.repository

import com.virtuous.common.util.suspendRunCatching
import com.virtuous.data.image.ImageResizer
import com.virtuous.datastore.datasource.token.LocalTokenDataSource
import com.virtuous.datastore.datasource.user.LocalUserDataSource
import com.virtuous.domain.model.auth.User
import com.virtuous.domain.model.auth.UserRole
import com.virtuous.domain.repository.AuthRepository
import com.virtuous.network.source.auth.AuthDataSource
import com.virtuous.network.source.notification.NotificationDataSource
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val localTokenDataSource: LocalTokenDataSource,
    private val localUserDataSource: LocalUserDataSource,
    private val notificationDataSource: NotificationDataSource,
    private val imageResizer: ImageResizer,
) : AuthRepository {
    override suspend fun loginKakao(idToken: String): Result<User> = suspendRunCatching {
        val response = authDataSource.loginKakao(idToken).getOrThrow()

        if (response.signupToken != null && response.providerId != null) {
            User(UserRole.NONE, response.signupToken, response.providerId)
        } else {
            coroutineScope {
                val accessTokenJob = launch {
                    response.accessToken?.let { localTokenDataSource.setAccessToken(it) }
                }

                val refreshTokenJob = launch {
                    response.refreshToken?.let { localTokenDataSource.setRefreshToken(it) }
                }

                joinAll(accessTokenJob, refreshTokenJob)
            }

            notificationDataSource.postDeviceToken()
            User(UserRole.USER)
        }
    }

    override suspend fun registerUser(
        signUpToken: String,
        providerId: String,
        nickName: String,
        profileImageUrl: String?
    ): Result<Unit> = suspendRunCatching {
        val uploadImageUrl = profileImageUrl?.let { imageResizer.resizeImage(profileImageUrl) }
        val response =
            authDataSource.registerUser(signUpToken, providerId, nickName, uploadImageUrl)
                .getOrThrow()

        coroutineScope {
            val accessTokenJob = launch {
                response.accessToken.let { localTokenDataSource.setAccessToken(it) }
            }

            val refreshTokenJob = launch {
                response.refreshToken.let { localTokenDataSource.setRefreshToken(it) }
            }

            joinAll(accessTokenJob, refreshTokenJob)
        }

        notificationDataSource.postDeviceToken()
    }

    override suspend fun logOut(): Result<Unit> = suspendRunCatching {
        authDataSource.logout().getOrThrow()

        coroutineScope {
            val clearTokenJob = launch {
                localTokenDataSource.clearToken()
            }

            val userInfoJob = launch {
                localUserDataSource.clearUserInfo()
            }

            joinAll(clearTokenJob, userInfoJob)
        }

    }

    override suspend fun unregisterUser(): Result<Unit> = suspendRunCatching {
        authDataSource.unregisterUser().getOrThrow()

        coroutineScope {
            val clearTokenJob = launch {
                localTokenDataSource.clearToken()
            }

            val userInfoJob = launch {
                localUserDataSource.clearUserInfo()
            }

            joinAll(clearTokenJob, userInfoJob)
        }
    }
}