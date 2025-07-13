package com.virtuous.network.source.mission

import android.os.Build
import com.virtuous.network.api.TraceApi
import com.virtuous.network.model.mission.DailyMissionResponse
import com.virtuous.network.model.mission.GetCompletedMissionsRequest
import com.virtuous.network.model.mission.GetCompletedMissionsResponse
import com.virtuous.network.model.mission.VerifyMissionRequest
import com.virtuous.network.model.post.PostResponse
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import java.util.UUID
import javax.inject.Inject

class MissionDataSourceImpl @Inject constructor(
    private val traceApi: TraceApi
) : MissionDataSource {
    override suspend fun getDailyMission(): Result<DailyMissionResponse> =
        traceApi.getDailyMission()

    override suspend fun getCompletedMissions(
        cursorDateTime: LocalDateTime?,
        size: Int
    ): Result<GetCompletedMissionsResponse> = traceApi.getCompletedMissions(
        GetCompletedMissionsRequest(
            cursorDateTime = cursorDateTime,
            size = size,
        )
    )

    override suspend fun changeDailyMission(): Result<DailyMissionResponse> =
        traceApi.changeDailyMission()

    override suspend fun verifyDailyMission(
        title: String,
        content: String,
        images: List<InputStream>?
    ): Result<PostResponse> {
        val jsonString = Json.encodeToString(
            VerifyMissionRequest(
                title = title,
                content = content
            )
        )

        val requestBody = jsonString
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val (imageFileExtension, imageFileName) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WEBP_MEDIA_TYPE to "mission_${UUID.randomUUID()}.webp"
        } else {
            JPEG_MEDIA_TYPE to "mission_${UUID.randomUUID()}.jpg"
        }

        val mediaType = imageFileExtension.toMediaTypeOrNull()
            ?: throw IllegalArgumentException("Invalid media type: $imageFileExtension")

        val requestImage = images?.map { image ->
            val body = image.readBytes().toRequestBody(mediaType)
            MultipartBody.Part.createFormData(
                name = "imageFiles",
                filename = imageFileName,
                body = body
            )
        }

        return traceApi.verifyDailyMission(
            verifyMissionRequest = requestBody,
            imageFiles = requestImage
        )
    }

    companion object {
        private const val WEBP_MEDIA_TYPE = "image/webp"
        private const val JPEG_MEDIA_TYPE = "image/jpeg"
    }
}