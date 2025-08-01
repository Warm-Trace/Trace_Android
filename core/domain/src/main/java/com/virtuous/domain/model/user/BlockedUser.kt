package com.virtuous.domain.model.user

import java.time.LocalDateTime

data class BlockedUser(
    val providerId: String,
    val name: String,
    val profileImageUrl: String? = null,
    val blockedAt: LocalDateTime,
)
