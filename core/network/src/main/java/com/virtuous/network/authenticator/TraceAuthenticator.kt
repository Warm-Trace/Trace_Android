package com.virtuous.network.authenticator

import android.util.Log
import com.virtuous.network.api.TraceApi
import com.virtuous.network.model.token.RefreshTokenRequest
import com.virtuous.network.token.TokenManager
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

class TraceAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val traceApi: Provider<TraceApi>
) : Authenticator {
    private val refreshMutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        val originRequest = response.request

        Log.d("traceAuthenticate", originRequest.toString())
        if (originRequest.header("Authorization")
                .isNullOrEmpty() && !originRequest.url.encodedPath.contains("/api/v1/token/expiration")
        ) {
            return null
        }

        if (originRequest.url.encodedPath.contains("/api/v1/token/refresh")) {
            runBlocking {
                tokenManager.setAccessToken("")
                tokenManager.setRefreshToken("")
            }

            return null
        }

        if (response.code != 401) {
            return null
        }

        val retryCount = originRequest.header(RETRY_HEADER)?.toIntOrNull() ?: 0
        if (retryCount >= MAX_RETRY_COUNT) {
            return null
        }

        val token = runBlocking {
            refreshMutex.withLock {
                traceApi.get().refreshToken(RefreshTokenRequest(tokenManager.getRefreshToken()))
            }
        }.getOrNull() ?: return null

        runBlocking {
            val accessTokenJob = launch { tokenManager.setAccessToken(token.accessToken) }
            val refreshTokenJob = launch { tokenManager.setRefreshToken(token.refreshToken) }
            joinAll(accessTokenJob, refreshTokenJob)
        }

        if (originRequest.url.encodedPath.contains("/api/v1/token/expiration")) {
            val newAccessToken = runBlocking {
                tokenManager.getAccessToken()
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val newBody = "{\"token\":\"$newAccessToken\"}".toRequestBody(mediaType)

            return originRequest.newBuilder()
                .header(RETRY_HEADER, (retryCount + 1).toString())
                .method("POST", newBody)
                .build()
        }

        val newRequest = originRequest.newBuilder()
            .header(RETRY_HEADER, (retryCount + 1).toString())
            .header("Authorization", "Bearer ${token.accessToken}")
            .build()

        return newRequest
    }

    companion object {
        private const val MAX_RETRY_COUNT = 3
        private const val RETRY_HEADER = "Retry-Count"
    }

}