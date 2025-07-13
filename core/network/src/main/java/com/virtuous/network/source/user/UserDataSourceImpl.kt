package com.virtuous.network.source.user

import android.os.Build
import com.virtuous.network.api.TraceApi
import com.virtuous.network.model.user.LoadUserInfoResponse
import com.virtuous.network.model.user.UpdateNicknameRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import java.util.UUID
import javax.inject.Inject

class UserDataSourceImpl @Inject constructor(
    private val traceApi: TraceApi,
) : UserDataSource {
    override suspend fun loadUserInfo(): Result<LoadUserInfoResponse> = traceApi.loadUserInfo()
    override suspend fun updateNickname(nickname: String): Result<LoadUserInfoResponse> =
        traceApi.updateNickname(
            updateNicknameRequest = UpdateNicknameRequest(nickname)
        )

    override suspend fun updateProfileImage(profileImage: InputStream?): Result<LoadUserInfoResponse> {
        val (imageFileExtension, imageFileName) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WEBP_MEDIA_TYPE to "profile_${UUID.randomUUID()}.webp"
        } else {
            JPEG_MEDIA_TYPE to "profile_${UUID.randomUUID()}.jpg"
        }

        val mediaType = imageFileExtension.toMediaTypeOrNull()
            ?: throw IllegalArgumentException("Invalid media type: $imageFileExtension")


        val requestImage = if (profileImage != null) {
            MultipartBody.Part.createFormData(
                name = "profileImage",
                filename = imageFileName,
                body = profileImage.readBytes().toRequestBody(mediaType)
            )
        } else MultipartBody.Part.createFormData(
            name = "profileImage",
            filename = "",
            body = ByteArray(0).toRequestBody("application/octet-stream".toMediaTypeOrNull())
        )

        return traceApi.updateProfileImage(requestImage)
    }

    companion object {
        private const val WEBP_MEDIA_TYPE = "image/webp"
        private const val JPEG_MEDIA_TYPE = "image/jpeg"
    }
}