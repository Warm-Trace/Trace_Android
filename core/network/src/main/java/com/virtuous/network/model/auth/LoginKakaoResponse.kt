package com.virtuous.network.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginKakaoResponse(
    val signupToken : String? = null,
    val providerId : String? = null,
    val isRegistered : Boolean = false,
    val accessToken : String? = null,
    val refreshToken : String? = null
)

