package com.virtuous.data.repository

import com.virtuous.datastore.datasource.token.LocalTokenDataSource
import com.virtuous.network.token.TokenManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class TokenManagerImpl @Inject constructor(
    private val localTokenDataSource: LocalTokenDataSource,
) : TokenManager {
    override suspend fun getAccessToken(): String = localTokenDataSource.accessToken.first()

    override suspend fun getRefreshToken(): String = localTokenDataSource.refreshToken.first()

    override suspend fun setAccessToken(accessToken: String) =
        localTokenDataSource.setAccessToken(accessToken)

    override suspend fun setRefreshToken(refreshToken: String) =
        localTokenDataSource.setRefreshToken(refreshToken)
}