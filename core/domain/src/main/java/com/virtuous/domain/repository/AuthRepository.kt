package com.virtuous.domain.repository

import com.virtuous.domain.model.auth.User

interface AuthRepository {
    suspend fun loginKakao(idToken: String): Result<User>

    suspend fun registerUser(
        signUpToken: String,
        providerId: String,
        nickName: String,
        profileImageUrl: String?
    ): Result<Unit>

    suspend fun logOut(): Result<Unit>

    suspend fun unregisterUser(): Result<Unit>
}