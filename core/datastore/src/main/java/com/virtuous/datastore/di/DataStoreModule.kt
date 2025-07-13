package com.virtuous.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.virtuous.datastore.datasource.keyword.LocalKeywordDataSource
import com.virtuous.datastore.datasource.keyword.LocalKeywordDataSourceImpl
import com.virtuous.datastore.datasource.token.LocalTokenDataSource
import com.virtuous.datastore.datasource.token.LocalTokenDataSourceImpl
import com.virtuous.datastore.datasource.user.LocalUserDataSource
import com.virtuous.datastore.datasource.user.LocalUserDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreProvidesModule {
    private const val TOKEN_DATASTORE_NAME = "TOKEN_PREFERENCES"
    private val Context.tokenDataStore by preferencesDataStore(name = TOKEN_DATASTORE_NAME)

    private const val KEYWORD_DATASTORE_NAME = "KEYWORD_PREFERENCES"
    private val Context.keywordDataStore by preferencesDataStore(name = KEYWORD_DATASTORE_NAME)

    private const val USER_DATASTORE_NAME = "USER_PREFERENCES"
    private val Context.userDataStore by preferencesDataStore(name = USER_DATASTORE_NAME)

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    @Named("token")
    fun provideTokenDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.tokenDataStore

    @Provides
    @Singleton
    @Named("keyword")
    fun provideKeywordDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.keywordDataStore

    @Provides
    @Singleton
    @Named("user")
    fun provideUserDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.userDataStore
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DatastoreBindsModule {

    @Binds
    @Singleton
    abstract fun bindsLocalTokenDataSource(
        localTokenDataSourceImpl: LocalTokenDataSourceImpl,
    ): LocalTokenDataSource

    @Binds
    @Singleton
    abstract fun bindsLocalKeywordDataSource(
        localKeywordDataSourceImpl: LocalKeywordDataSourceImpl,
    ): LocalKeywordDataSource

    @Binds
    @Singleton
    abstract fun bindsUserDataSource(
        localUserDataSourceImpl: LocalUserDataSourceImpl,
    ): LocalUserDataSource
}
