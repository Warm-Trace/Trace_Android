package com.virtuous.network.di

import com.google.firebase.messaging.FirebaseMessaging
import com.virtuous.network.source.auth.AuthDataSource
import com.virtuous.network.source.auth.AuthDataSourceImpl
import com.virtuous.network.source.comment.CommentDataSource
import com.virtuous.network.source.comment.CommentDataSourceImpl
import com.virtuous.network.source.mission.MissionDataSource
import com.virtuous.network.source.mission.MissionDataSourceImpl
import com.virtuous.network.source.notification.NotificationDataSource
import com.virtuous.network.source.notification.NotificationDataSourceImpl
import com.virtuous.network.source.post.PostDataSource
import com.virtuous.network.source.post.PostDataSourceImpl
import com.virtuous.network.source.search.SearchDataSource
import com.virtuous.network.source.search.SearchDataSourceImpl
import com.virtuous.network.source.user.UserDataSource
import com.virtuous.network.source.user.UserDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {
    @Binds
    @Singleton
    abstract fun bindsNotificationDataSource(notificationDataSourceImpl: NotificationDataSourceImpl): NotificationDataSource

    @Binds
    @Singleton
    abstract fun bindsAuthDataSource(authDataSourceImpl: AuthDataSourceImpl): AuthDataSource

    @Binds
    @Singleton
    abstract fun bindsUserDataSource(userDataSourceImpl: UserDataSourceImpl): UserDataSource

    @Binds
    @Singleton
    abstract fun bindsPostDataSource(postDataSourceImpl: PostDataSourceImpl): PostDataSource

    @Binds
    @Singleton
    abstract fun bindsCommentDataSource(commentDataSourceImpl: CommentDataSourceImpl): CommentDataSource

    @Binds
    @Singleton
    abstract fun bindsSearchDataSource(searchDataSourceImpl: SearchDataSourceImpl): SearchDataSource

    @Binds
    @Singleton
    abstract fun bindsMissionDataSource(missionDataSourceImpl: MissionDataSourceImpl): MissionDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkProvidesModule {
    @Provides
    @Singleton
    fun provideFirebaseMessaging(): FirebaseMessaging = FirebaseMessaging.getInstance()
}

