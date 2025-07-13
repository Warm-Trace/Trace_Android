package com.virtuous.domain.repository

import androidx.paging.PagingData
import com.virtuous.domain.model.mypage.MyPageTab
import com.virtuous.domain.model.post.Emotion
import com.virtuous.domain.model.post.HomeTab
import com.virtuous.domain.model.post.PostDetail
import com.virtuous.domain.model.post.PostFeed
import com.virtuous.domain.model.post.WritePostType
import kotlinx.coroutines.flow.Flow


interface PostRepository {
    fun getPosts(tabType: HomeTab): Flow<PagingData<PostFeed>>

    fun getMyPosts(tabType: MyPageTab) : Flow<PagingData<PostFeed>>

    suspend fun getPost(postId: Int): Result<PostDetail>

    suspend fun addPost(
        postType: WritePostType,
        title: String,
        content: String,
        images: List<String>?
    ): Result<PostDetail>

    suspend fun verifyAndAddPost(
        title: String,
        content: String,
        images: List<String>?
    ) : Result<PostDetail>

    suspend fun updatePost(
        postId: Int,
        title: String,
        content: String,
        images: List<String>?
    ): Result<PostDetail>

    suspend fun deletePost(postId: Int): Result<Unit>

    suspend fun reportPost(postId: Int, reason: String): Result<Unit>

    suspend fun toggleEmotion(postId: Int, emotionType: Emotion): Result<Boolean>
}