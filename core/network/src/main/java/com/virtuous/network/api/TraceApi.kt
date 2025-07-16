package com.virtuous.network.api

import com.virtuous.network.model.auth.LoginKakaoRequest
import com.virtuous.network.model.auth.LoginKakaoResponse
import com.virtuous.network.model.auth.TokenResponse
import com.virtuous.network.model.comment.AddCommentRequest
import com.virtuous.network.model.comment.AddReplyToCommentRequest
import com.virtuous.network.model.comment.CommentResponse
import com.virtuous.network.model.comment.GetCommentsRequest
import com.virtuous.network.model.comment.GetCommentsResponse
import com.virtuous.network.model.mission.DailyMissionResponse
import com.virtuous.network.model.mission.GetCompletedMissionsRequest
import com.virtuous.network.model.mission.GetCompletedMissionsResponse
import com.virtuous.network.model.notification.PostDeviceTokenRequest
import com.virtuous.network.model.post.GetMyPostsRequest
import com.virtuous.network.model.post.GetPostsRequest
import com.virtuous.network.model.post.GetPostsResponse
import com.virtuous.network.model.post.PostResponse
import com.virtuous.network.model.post.ToggleEmotionRequest
import com.virtuous.network.model.post.ToggleEmotionResponse
import com.virtuous.network.model.post.UpdatePostRequest
import com.virtuous.network.model.report.ReportContentRequest
import com.virtuous.network.model.search.SearchPostsRequest
import com.virtuous.network.model.token.CheckTokenHealthRequest
import com.virtuous.network.model.token.CheckTokenHealthResponse
import com.virtuous.network.model.token.RefreshTokenRequest
import com.virtuous.network.model.user.LoadUserInfoResponse
import com.virtuous.network.model.user.UpdateNicknameRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface TraceApi {
    // 회원 관리
    @POST("/api/v1/auth/oauth/login")
    suspend fun loginKakao(@Body loginKakaoRequest: LoginKakaoRequest): Result<LoginKakaoResponse>

    @Multipart
    @POST("/api/v1/auth/oauth/signup")
    suspend fun registerUser(
        @Part("request") registerUserRequest: RequestBody,
        @Part profileImage: MultipartBody.Part? = null
    ): Result<TokenResponse>

    @POST("/api/v1/user/logout")
    suspend fun logout(): Result<Unit>

    @POST("/api/v1/user/delete")
    suspend fun unregisterUser(): Result<Unit>

    @GET("/api/v1/user")
    suspend fun loadUserInfo(): Result<LoadUserInfoResponse>

    @PUT("/api/v1/user/profile/nickname")
    suspend fun updateNickname(
        @Body updateNicknameRequest : UpdateNicknameRequest
    ) : Result<LoadUserInfoResponse>

    @Multipart
    @PUT("/api/v1/user/profile/image")
    suspend fun updateProfileImage(
        @Part profileImage: MultipartBody.Part
    ) : Result<LoadUserInfoResponse>

    // 토큰
    @HTTP(method = "POST", path = "/api/v1/token/refresh", hasBody = true)
    suspend fun refreshToken(@Body refreshTokenRequest: RefreshTokenRequest): Result<TokenResponse>

    @POST("/api/v1/token/expiration")
    suspend fun checkTokenHealth(@Body checkTokenHealthRequest: CheckTokenHealthRequest): Result<CheckTokenHealthResponse>

    // 게시글
    @POST("/api/v1/posts/feed")
    suspend fun getPosts(@Body getPostsRequest: GetPostsRequest): Result<GetPostsResponse>

    @GET("/api/v1/posts/{id}")
    suspend fun getPost(@Path("id") postId: Int): Result<PostResponse>

    @Multipart
    @POST("/api/v1/posts")
    suspend fun addPost(
        @Part("request") addPostRequest: RequestBody,
        @Part imageFiles: List<MultipartBody.Part>? = null
    ): Result<PostResponse>

    @Multipart
    @POST("/api/v1/posts/verify")
    suspend fun verifyAndAddPost(
        @Part("request") verifyAndAddPostRequest: RequestBody,
        @Part imageFiles: List<MultipartBody.Part>? = null
    ): Result<PostResponse>

    @PUT("/api/v1/posts/{id}")
    suspend fun updatePost(
        @Path("id") postId: Int,
        @Body updatePostRequest: UpdatePostRequest
    ): Result<PostResponse>

    @DELETE("/api/v1/posts/{id}")
    suspend fun deletePost(
        @Path("id") postId: Int,
    ): Result<Unit>

    @POST("/api/v1/emotion/{postId}")
    suspend fun toggleEmotion(
        @Path("postId") postId: Int,
        @Body toggleEmotionRequest: ToggleEmotionRequest,
    ): Result<ToggleEmotionResponse>

    // 댓글
    @POST("/api/v1/comments/{postId}/cursor")
    suspend fun getComments(
        @Path("postId") postId: Int,
        @Body getCommentsRequest: GetCommentsRequest
    ) : Result<GetCommentsResponse>

    @POST("/api/v1/comments/{postId}")
    suspend fun addComment(
        @Path("postId") postId: Int,
        @Body addCommentRequest: AddCommentRequest,
    ): Result<CommentResponse>

    @POST("/api/v1/comments/{postId}/{commentId}")
    suspend fun addReplyToComment(
        @Path("postId") postId: Int,
        @Path("commentId") commentId: Int,
        @Body addReplyToCommentRequest: AddReplyToCommentRequest,
    ): Result<CommentResponse>

    @DELETE("/api/v1/comments/{commentId}")
    suspend fun deleteComment(
        @Path("commentId") commentId: Int,
    ): Result<Unit>

    // 검색
    @POST("/api/v1/posts/search")
    suspend fun searchPosts(
        @Body searchPostsRequest : SearchPostsRequest
    ) : Result<GetPostsResponse>

    // 미션
    @GET("/api/v1/missions/today")
    suspend fun getDailyMission() : Result<DailyMissionResponse>

    @POST("/api/v1/missions/completed")
    suspend fun getCompletedMissions(@Body getCompletedMissionsRequest: GetCompletedMissionsRequest) : Result<GetCompletedMissionsResponse>

    @POST("/api/v1/missions/change")
    suspend fun changeDailyMission() : Result<DailyMissionResponse>

    @Multipart
    @POST("/api/v1/missions/submit")
    suspend fun verifyDailyMission(
        @Part("request") verifyMissionRequest: RequestBody,
        @Part imageFiles: List<MultipartBody.Part>? = null
    ): Result<PostResponse>

    // 신고
    @POST("/api/v1/reports")
    suspend fun reportContent(@Body reportContentRequest: ReportContentRequest): Result<Unit>

    //차단
    @POST("/api/v1/reports/block/{blockedProviderId}")
    suspend fun blockUser(
        @Path("blockedProviderId") blockedProviderId: String
    ) : Result<Unit>

    // 마이페이지
    @POST("/api/v1/user/myPosts")
    suspend fun getMyPosts(@Body getMyPostsRequest: GetMyPostsRequest): Result<GetPostsResponse>

    // 알림
    @POST("/api/v1/fcm/tokens")
    suspend fun postDeviceToken(
        @Body postDeviceTokenRequest: PostDeviceTokenRequest
    ): Result<Unit>

}
