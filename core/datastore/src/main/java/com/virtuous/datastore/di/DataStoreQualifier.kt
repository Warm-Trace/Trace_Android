package com.virtuous.datastore.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TokenDataSource

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class KeywordDataSource

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UserDataSource