package com.virtuous.domain.model.user

data class UserInfo(
    val name : String,
    val profileImageUrl : String? = null,
    val verificationScore : Int,
    val verificationCount : Int
)
