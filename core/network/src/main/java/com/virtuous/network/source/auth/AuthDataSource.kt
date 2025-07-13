package com.virtuous.network.source.auth

import com.virtuous.network.model.auth.LoginKakaoResponse
import com.virtuous.network.model.auth.TokenResponse
import com.virtuous.network.model.token.CheckTokenHealthResponse
import java.io.InputStream

interface AuthDataSource {
    suspend fun loginKakao(idToken: String): Result<LoginKakaoResponse>

    suspend fun registerUser(
        signUpToken : String,
        providerId : String,
        nickname: String,
        profileImage: InputStream? = null
    ): Result<TokenResponse>

    suspend fun checkTokenHealth(token : String) : Result<CheckTokenHealthResponse>

    suspend fun logout() : Result<Unit>

    suspend fun unregisterUser() : Result<Unit>
}