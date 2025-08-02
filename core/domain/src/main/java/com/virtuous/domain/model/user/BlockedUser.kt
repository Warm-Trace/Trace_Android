package com.virtuous.domain.model.user

data class BlockedUser(
    val providerId: String,
    val name: String,
    val profileImageUrl: String? = null,
)
