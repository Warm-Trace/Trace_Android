package com.virtuous.network.source.post


import com.virtuous.domain.model.mypage.MyPageTab
import com.virtuous.domain.model.post.Emotion
import com.virtuous.domain.model.post.HomeTab
import com.virtuous.domain.model.post.WritePostType
import com.virtuous.network.model.post.GetPostsResponse
import com.virtuous.network.model.post.PostResponse
import com.virtuous.network.model.post.ToggleEmotionResponse
import kotlinx.datetime.LocalDateTime
import java.io.InputStream

interface PostDataSource {
    suspend fun getPosts(
        cursorDateTime : LocalDateTime?,
        cursorId : Int?,
        size : Int,
        postType : HomeTab
    ) : Result<GetPostsResponse>

    suspend fun getMyPosts(
        cursorDateTime : LocalDateTime?,
        cursorId : Int?,
        size : Int,
        tabType: MyPageTab
    ) : Result<GetPostsResponse>

    suspend fun getPost(
        postId : Int
    ) : Result<PostResponse>

    suspend fun addPost(
        postType : WritePostType, title: String, content: String, images: List<InputStream>?
    ): Result<PostResponse>

    suspend fun verifyAndAddPost(
        title: String, content: String, images: List<InputStream>?
    ) : Result<PostResponse>

    suspend fun updatePost(
        postId : Int, title: String, content: String, images: List<InputStream>?,
    ) : Result<PostResponse>

    suspend fun deletePost(
        postId : Int,
    ) : Result<Unit>

    suspend fun toggleEmotion(
        postId : Int,
        emotionType : Emotion
    ) : Result<ToggleEmotionResponse>

    suspend fun reportPost(
        postId : Int,
        reason : String
    ) : Result<Unit>
}
