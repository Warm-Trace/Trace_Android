package com.virtuous.domain.repository

import androidx.paging.PagingData
import com.virtuous.domain.model.home.UserProfileTab
import com.virtuous.domain.model.mypage.MyPageTab
import com.virtuous.domain.model.post.Emotion
import com.virtuous.domain.model.post.HomeTab
import com.virtuous.domain.model.post.PostDetail
import com.virtuous.domain.model.post.PostFeed
import com.virtuous.domain.model.post.WritePostType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow



interface PostRepository {
    val postUpdateEvents: SharedFlow<PostUpdateEvent>

    fun getPosts(tabType: HomeTab): Flow<PagingData<PostFeed>>

    fun getMyPosts(tabType: MyPageTab) : Flow<PagingData<PostFeed>>

    fun getUserPosts(providerId: String, tabType: UserProfileTab) : Flow<PagingData<PostFeed>>

    suspend fun getPost(postId: Int): Result<PostDetail>

    suspend fun addPost(
        postType: WritePostType,
        title: String,
        content: String,
        images: List<String>
    ): Result<PostDetail>

    suspend fun verifyAndAddPost(
        title: String,
        content: String,
        images: List<String>
    ) : Result<PostDetail>

    suspend fun updatePost(
        postId: Int,
        title: String,
        content: String,
        removedImages : List<String>,
        images: List<String>
    ): Result<PostDetail>

    suspend fun deletePost(postId: Int): Result<Unit>

    suspend fun reportPost(postId: Int, reason: String): Result<Unit>

    suspend fun toggleEmotion(postId: Int, emotionType: Emotion): Result<Boolean>

    suspend fun blockUser(providerId : String) : Result<Unit>
}

sealed class PostUpdateEvent {
    data class PostDeleted(val postId: Int) : PostUpdateEvent()
    data class PostUpdated(val postId: Int, val postDetail: PostDetail) : PostUpdateEvent()
    data class UserBlocked(val providerId: String) : PostUpdateEvent()
}