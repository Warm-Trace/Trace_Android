package com.example.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.common.util.suspendRunCatching
import com.example.data.image.ImageResizer
import com.example.data.paging.MyPostPagingSource
import com.example.data.paging.PostPagingSource
import com.example.domain.model.mypage.MyPageTab
import com.example.domain.model.post.Emotion
import com.example.domain.model.post.HomeTab
import com.example.domain.model.post.PostDetail
import com.example.domain.model.post.PostFeed
import com.example.domain.model.post.WritePostType
import com.example.domain.repository.PostRepository
import com.example.network.source.post.PostDataSource
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class PostRepositoryImpl @Inject constructor(
    private val postDataSource: PostDataSource,
    private val imageResizer: ImageResizer,
) : PostRepository {
    override fun getPosts(tabType: HomeTab): Flow<PagingData<PostFeed>> {
        return Pager(
            config = PagingConfig(pageSize = DEFAULT_PAGE_SIZE),
            pagingSourceFactory = {
                PostPagingSource(postDataSource, tabType)
            }
        ).flow
    }

    override fun getMyPosts(tabType: MyPageTab): Flow<PagingData<PostFeed>> {
        return Pager(
            config = PagingConfig(pageSize = DEFAULT_PAGE_SIZE),
            pagingSourceFactory = {
                MyPostPagingSource(postDataSource, tabType)
            }
        ).flow
    }

    override suspend fun getPost(postId: Int): Result<PostDetail> = suspendRunCatching {
        postDataSource.getPost(postId).getOrThrow().toDomain()
    }

    override suspend fun addPost(
        postType: WritePostType,
        title: String,
        content: String,
        images: List<String>?
    ): Result<PostDetail> = suspendRunCatching {
        val imageStreams = images?.map { imageUrl ->
            imageResizer.resizeImage(imageUrl)
        }

        val response = postDataSource.addPost(postType, title, content, imageStreams).getOrThrow()

        response.toDomain()
    }

    override suspend fun verifyAndAddPost(
        title: String,
        content: String,
        images: List<String>?
    ): Result<PostDetail> = suspendRunCatching {
        val imageStreams = images?.map { imageUrl ->
            imageResizer.resizeImage(imageUrl)
        }

        val response = postDataSource.verifyAndAddPost(title, content, imageStreams).getOrThrow()

        response.toDomain()
    }

    override suspend fun updatePost(
        postId: Int,
        title: String,
        content: String,
        images: List<String>?
    ): Result<PostDetail> = suspendRunCatching {
//        val imageStreams = images?.mapIndexed { index, imageUrl ->
//            imageResizer.resizeImage(imageUrl)
//        }

        val response = postDataSource.updatePost(postId, title, content, null).getOrThrow()

        response.toDomain()
    }

    override suspend fun deletePost(postId: Int): Result<Unit> = suspendRunCatching {
        postDataSource.deletePost(postId)
    }

    override suspend fun toggleEmotion(postId: Int, emotionType: Emotion): Result<Boolean> =
        suspendRunCatching {
            val response = postDataSource.toggleEmotion(postId, emotionType).getOrThrow()

            response.isAdded
        }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }
}
