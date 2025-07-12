package com.example.network.source.post


import com.example.domain.model.mypage.MyPageTab
import com.example.domain.model.post.Emotion
import com.example.domain.model.post.HomeTab
import com.example.domain.model.post.WritePostType
import com.example.network.model.post.GetPostsResponse
import com.example.network.model.post.PostResponse
import com.example.network.model.post.ToggleEmotionResponse
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
