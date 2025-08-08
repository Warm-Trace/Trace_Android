package com.virtuous.network.model.user

import com.virtuous.domain.model.user.BlockedUser
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class GetBlockedUsersResponse(
    val providerId: String,
    val nickname: String,
    val profileImageUrl: String?,
    val blockedAt: LocalDateTime
) {
    fun toDomain(): BlockedUser = BlockedUser(
        providerId = providerId,
        name = nickname,
        profileImageUrl = profileImageUrl,
    )
}