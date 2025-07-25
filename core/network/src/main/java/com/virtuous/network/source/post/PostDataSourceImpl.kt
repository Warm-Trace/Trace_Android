package com.virtuous.network.source.post

import android.os.Build
import com.virtuous.domain.model.mypage.MyPageTab
import com.virtuous.domain.model.post.Emotion
import com.virtuous.domain.model.post.HomeTab
import com.virtuous.domain.model.post.PostType
import com.virtuous.domain.model.post.WritePostType
import com.virtuous.network.api.TraceApi
import com.virtuous.network.model.post.AddPostRequest
import com.virtuous.network.model.post.GetMyPostsRequest
import com.virtuous.network.model.post.GetPostsRequest
import com.virtuous.network.model.post.GetPostsResponse
import com.virtuous.network.model.post.PostResponse
import com.virtuous.network.model.post.ToggleEmotionRequest
import com.virtuous.network.model.post.ToggleEmotionResponse
import com.virtuous.network.model.post.UpdatePostRequest
import com.virtuous.network.model.post.VerifyAndAddPostRequest
import com.virtuous.network.model.report.ReportContentRequest
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import java.util.UUID
import javax.inject.Inject

class PostDataSourceImpl @Inject constructor(
    private val traceApi: TraceApi,
) : PostDataSource {
    override suspend fun getPosts(
        cursorDateTime: LocalDateTime?,
        cursorId: Int?,
        size: Int,
        postType: HomeTab
    ): Result<GetPostsResponse> = traceApi.getPosts(
        getPostsRequest = GetPostsRequest(
            cursorDateTime = cursorDateTime,
            cursorId = cursorId,
            size = size,
            postType = if (postType == HomeTab.ALL) null else postType.name
        )
    )

    override suspend fun getMyPosts(
        cursorDateTime: LocalDateTime?,
        cursorId: Int?,
        size: Int,
        tabType: MyPageTab
    ): Result<GetPostsResponse> = traceApi.getMyPosts(
        getMyPostsRequest = GetMyPostsRequest(
            cursorDateTime = cursorDateTime,
            cursorId = cursorId,
            size = size,
            myPageTab = tabType.name
        )
    )

    override suspend fun getPost(postId: Int): Result<PostResponse> = traceApi.getPost(postId)

    override suspend fun addPost(
        postType: WritePostType,
        title: String,
        content: String,
        images: List<InputStream>
    ): Result<PostResponse> {
        val jsonString = Json.encodeToString(
            AddPostRequest(
                postType = postType.name,
                title = title,
                content = content
            )
        )

        val requestBody = jsonString
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val (imageFileExtension, imageFileName) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WEBP_MEDIA_TYPE to "post_${UUID.randomUUID()}.webp"
        } else {
            JPEG_MEDIA_TYPE to "post_${UUID.randomUUID()}.jpg"
        }

        val mediaType = imageFileExtension.toMediaTypeOrNull()
            ?: throw IllegalArgumentException("Invalid media type: $imageFileExtension")

        val requestImage = images.map { image ->
            val body = image.readBytes().toRequestBody(mediaType)
            MultipartBody.Part.createFormData(
                name = "imageFiles",
                filename = imageFileName,
                body = body
            )
        }

        return traceApi.addPost(
            addPostRequest = requestBody,
            imageFiles = requestImage
        )
    }

    override suspend fun verifyAndAddPost(
        title: String,
        content: String,
        images: List<InputStream>
    ): Result<PostResponse> {
        val jsonString = Json.encodeToString(
            VerifyAndAddPostRequest(
                postType = PostType.GOOD_DEED.name,
                title = title,
                content = content
            )
        )

        val requestBody = jsonString
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val (imageFileExtension, imageFileName) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WEBP_MEDIA_TYPE to "post_${UUID.randomUUID()}.webp"
        } else {
            JPEG_MEDIA_TYPE to "post_${UUID.randomUUID()}.jpg"
        }

        val mediaType = imageFileExtension.toMediaTypeOrNull()
            ?: throw IllegalArgumentException("Invalid media type: $imageFileExtension")

        val requestImage = images.map { image ->
            val body = image.readBytes().toRequestBody(mediaType)
            MultipartBody.Part.createFormData(
                name = "imageFiles",
                filename = imageFileName,
                body = body
            )
        }

        return traceApi.verifyAndAddPost(
            verifyAndAddPostRequest = requestBody,
            imageFiles = requestImage
        )
    }

    override suspend fun updatePost(
        postId: Int,
        title: String,
        content: String,
        removedImages: List<String>,
        images: List<InputStream>
    ): Result<PostResponse> {
        val jsonString = Json.encodeToString(
            UpdatePostRequest(
                title = title,
                content = content,
                removal = removedImages
            )
        )

        val requestBody = jsonString
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val (imageFileExtension, imageFileName) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WEBP_MEDIA_TYPE to "post_${UUID.randomUUID()}.webp"
        } else {
            JPEG_MEDIA_TYPE to "post_${UUID.randomUUID()}.jpg"
        }

        val mediaType = imageFileExtension.toMediaTypeOrNull()
            ?: throw IllegalArgumentException("Invalid media type: $imageFileExtension")

        val requestImage = images.map { image ->
            val body = image.readBytes().toRequestBody(mediaType)
            MultipartBody.Part.createFormData(
                name = "imageFiles",
                filename = imageFileName,
                body = body
            )
        }

        return traceApi.updatePost(
            postId = postId,
            updatePostRequest = requestBody,
            imageFiles = requestImage
        )
    }

    override suspend fun deletePost(postId: Int): Result<Unit> = traceApi.deletePost(postId)

    override suspend fun toggleEmotion(
        postId: Int,
        emotionType: Emotion
    ): Result<ToggleEmotionResponse> =
        traceApi.toggleEmotion(
            postId = postId,
            toggleEmotionRequest = ToggleEmotionRequest(
                emotionType = emotionType.name
            )
        )

    override suspend fun reportPost(postId: Int, reason: String): Result<Unit> =
        traceApi.reportContent(
            reportContentRequest = ReportContentRequest(
                postId = postId,
                commentId = null,
                reason = reason,
            )
        )

    override suspend fun blockUser(providerId: String): Result<Unit> =
        traceApi.blockUser(blockedProviderId = providerId)

    companion object {
        private const val WEBP_MEDIA_TYPE = "image/webp"
        private const val JPEG_MEDIA_TYPE = "image/jpeg"
    }
}