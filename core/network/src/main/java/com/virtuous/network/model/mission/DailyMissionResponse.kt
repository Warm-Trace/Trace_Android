package com.virtuous.network.model.mission

import com.virtuous.domain.model.mission.DailyMission
import com.virtuous.domain.model.mission.Mission
import kotlinx.serialization.Serializable

@Serializable
data class DailyMissionResponse(
    val content: String,
    val changeCount: Int,
    val isVerified : Boolean,
) {
    fun toDomain(): DailyMission {
        return DailyMission(
            mission = Mission(content, isVerified),
            changeCount = changeCount
        )
    }
}
