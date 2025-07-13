package com.virtuous.network.model.user

import kotlinx.serialization.Serializable

@Serializable
data class LoadUserInfoResponse(
    val nickname : String,
    val profileImageUrl : String? = null,
    val verificationScore : Int = 0,
    val verificationCount : Int = 0,
)
