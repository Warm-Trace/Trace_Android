package com.virtuous.network.source.mission

import com.virtuous.network.model.mission.DailyMissionResponse
import com.virtuous.network.model.mission.GetCompletedMissionsResponse
import com.virtuous.network.model.post.PostResponse
import kotlinx.datetime.LocalDateTime
import java.io.InputStream

interface MissionDataSource {
    suspend fun getDailyMission(): Result<DailyMissionResponse>

    suspend fun getCompletedMissions(
        cursorDateTime : LocalDateTime?,
        size : Int,
    ) : Result<GetCompletedMissionsResponse>

    suspend fun changeDailyMission(): Result<DailyMissionResponse>

    suspend fun verifyDailyMission(
        title: String,
        content: String,
        images: List<InputStream>?
    ): Result<PostResponse>
}