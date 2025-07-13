package com.virtuous.network.source.auth

import android.os.Build
import com.virtuous.network.api.TraceApi
import com.virtuous.network.model.auth.LoginKakaoRequest
import com.virtuous.network.model.auth.LoginKakaoResponse
import com.virtuous.network.model.auth.RegisterUserRequest
import com.virtuous.network.model.auth.TokenResponse
import com.virtuous.network.model.token.CheckTokenHealthRequest
import com.virtuous.network.model.token.CheckTokenHealthResponse
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthDataSourceImpl @Inject constructor(
    private val traceApi: TraceApi,
) : AuthDataSource {
    override suspend fun loginKakao(idToken: String): Result<LoginKakaoResponse> =
        traceApi.loginKakao(
            loginKakaoRequest = LoginKakaoRequest(
                idToken = idToken
            )
        )

    override suspend fun registerUser(
        signUpToken: String,
        providerId: String,
        nickname: String,
        profileImage: InputStream?
    ): Result<TokenResponse> {
        val jsonString = Json.encodeToString(
            RegisterUserRequest(
                signupToken = signUpToken,
                providerId = providerId,
                nickname = nickname
            )
        )

        val requestBody = jsonString
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val (imageFileExtension, imageFileName) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WEBP_MEDIA_TYPE to "profile_${UUID.randomUUID()}.webp"
        } else {
            JPEG_MEDIA_TYPE to "profile_${UUID.randomUUID()}.jpg"
        }

        val mediaType = imageFileExtension.toMediaTypeOrNull()
            ?: throw IllegalArgumentException("Invalid media type: $imageFileExtension")

        val requestImage = profileImage?.let {
            MultipartBody.Part.createFormData(
                name = "profileImage",
                filename = imageFileName,
                body = profileImage.readBytes().toRequestBody(mediaType)
            )
        }

        return traceApi.registerUser(
            registerUserRequest = requestBody,
            profileImage = requestImage
        )
    }

    override suspend fun checkTokenHealth(token: String): Result<CheckTokenHealthResponse> =
        traceApi.checkTokenHealth(
            CheckTokenHealthRequest(token)
        )

    override suspend fun logout(): Result<Unit> = traceApi.logout()

    override suspend fun unregisterUser(): Result<Unit> = traceApi.unregisterUser()

    companion object {
        private const val WEBP_MEDIA_TYPE = "image/webp"
        private const val JPEG_MEDIA_TYPE = "image/jpeg"
    }
}