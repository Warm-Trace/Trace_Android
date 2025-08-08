package com.virtuous.network.model.user

import kotlinx.serialization.Serializable

@Serializable
data class LoadUserVerificationsResponse(
    val verifiedPostCount: Int,
    val completedMissionCount: Int
)