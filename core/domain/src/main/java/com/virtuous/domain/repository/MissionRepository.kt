package com.virtuous.domain.repository

import androidx.paging.PagingData
import com.virtuous.domain.model.mission.DailyMission
import com.virtuous.domain.model.mission.MissionFeed
import kotlinx.coroutines.flow.Flow

interface MissionRepository {
    fun getCompletedMissions() : Flow<PagingData<MissionFeed>>
    suspend fun getDailyMission(): Result<DailyMission>
    suspend fun changeDailyMission(): Result<DailyMission>
    suspend fun verifyDailyMission(
        title: String,
        content: String,
        images: List<String>?
    ): Result<Int>
}