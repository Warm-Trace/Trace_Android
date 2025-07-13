package com.virtuous.domain.model.mission

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale

const val MAX_MISSION_CHANGE_COUNT = 10

data class DailyMission(
    val mission: Mission,
    val changeCount: Int = 0,
)

data class Mission(
    val description : String,
    val isVerified : Boolean,
)

data class MissionFeed(
    val missionId : Int,
    val description: String,
    val isVerified: Boolean,
    val imageUrl : String? = null,
    val createdAt : LocalDate
) {
    fun getFormattedDate() : String {
        val month = createdAt.monthValue
        val day = createdAt.dayOfMonth
        val dayOfWeek = createdAt.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN)

        return "${month}월 ${day}일 $dayOfWeek"
    }
}

