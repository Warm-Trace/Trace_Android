package com.virtuous.network.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class RegisterUserRequest(
    val signupToken: String,
    val providerId : String,
    val nickname: String,
)
