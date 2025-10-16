package com.virtuous.domain.model.user

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val name : String,
    val profileImageUrl : String? = null,
    val verificationScore : Int,
    val verificationCount : Int,
    val verifiedPostCount: Int,
    val completedMissionCount: Int
)
