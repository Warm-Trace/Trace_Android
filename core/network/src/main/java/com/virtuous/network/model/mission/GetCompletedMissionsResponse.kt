package com.virtuous.network.model.mission

import com.virtuous.domain.model.mission.MissionFeed
import com.virtuous.network.model.cursor.Cursor
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.serialization.Serializable

@Serializable
data class GetCompletedMissionsResponse(
    val hasNext: Boolean,
    val cursor: Cursor?,
    val content: List<MissionContent>,
) {
    fun toDomain(): List<MissionFeed> = content.map { it.toDomain() }
}

@Serializable
data class MissionContent(
    val postId: Int,
    val content: String,
    val changeCount : Int,
    val isVerified: Boolean,
    val imageUrl: String? = null,
    val createdAt: LocalDate,
) {
    fun toDomain(): MissionFeed {
        return MissionFeed(
            missionId = postId,
            description = content,
            isVerified = isVerified,
            imageUrl = imageUrl,
            createdAt = createdAt.toJavaLocalDate()
        )
    }
}