package com.virtuous.data.di

import com.virtuous.data.repository.AuthRepositoryImpl
import com.virtuous.data.repository.CommentRepositoryImpl
import com.virtuous.data.repository.MissionRepositoryImpl
import com.virtuous.data.repository.NotificationRepositoryImpl
import com.virtuous.data.repository.PostRepositoryImpl
import com.virtuous.data.repository.SearchRepositoryImpl
import com.virtuous.data.repository.TokenManagerImpl
import com.virtuous.data.repository.UserRepositoryImpl
import com.virtuous.domain.repository.AuthRepository
import com.virtuous.domain.repository.CommentRepository
import com.virtuous.domain.repository.MissionRepository
import com.virtuous.domain.repository.NotificationRepository
import com.virtuous.domain.repository.PostRepository
import com.virtuous.domain.repository.SearchRepository
import com.virtuous.domain.repository.UserRepository
import com.virtuous.network.token.TokenManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindsNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindsAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindsTokenManager(
        tokenManagerImpl: TokenManagerImpl,
    ): TokenManager

    @Binds
    @Singleton
    abstract fun bindsUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindsPostRepository(
        postRepositoryImpl: PostRepositoryImpl
    ): PostRepository

    @Binds
    @Singleton
    abstract fun bindsCommentRepository(
        commentRepositoryImpl: CommentRepositoryImpl
    ): CommentRepository


    @Binds
    @Singleton
    abstract fun bindsSearchRepository(
        searchRepositoryImpl: SearchRepositoryImpl
    ): SearchRepository

    @Binds
    @Singleton
    abstract fun bindsMissionRepository(
        missionRepositoryImpl: MissionRepositoryImpl
    ): MissionRepository
}
