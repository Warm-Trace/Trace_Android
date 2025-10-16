package com.virtuous.mission.graph.mission

import android.R.attr.description
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.virtuous.analytics.AnalyticsHelper
import com.virtuous.analytics.error.ErrorHelper
import com.virtuous.domain.model.mission.DailyMission
import com.virtuous.domain.model.mission.Mission
import com.virtuous.domain.repository.MissionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MissionViewModel @Inject constructor(
    private val missionRepository: MissionRepository,
    private val analyticsHelper: AnalyticsHelper,
    private val errorHelper: ErrorHelper,
) : ViewModel() {
    private val _eventChannel = Channel<MissionEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    private val _dailyMission = MutableStateFlow(
        DailyMission(
            mission = Mission("아직 미션이 할당되지 않았습니다", isVerified = false),
            changeCount = 0
        )
    )
    val dailyMission = _dailyMission.asStateFlow()

    val completedMissions = missionRepository.getCompletedMissions().cachedIn(viewModelScope)

    init {
        getDailyMission()
    }

    fun getDailyMission() = viewModelScope.launch {
        missionRepository.getDailyMission().onSuccess { dailyMission ->
            _dailyMission.value = dailyMission
        }.onFailure {
            errorHelper.logError(it)
        }
    }

    fun changeDailyMission() = viewModelScope.launch {
        missionRepository.changeDailyMission().onSuccess { dailyMission ->
            val originMissionDescription = _dailyMission.value.mission.description
            _dailyMission.value = dailyMission
            analyticsHelper.trackActionEvent(
                screenName = "mission",
                actionName = "change_daily_mission",
                properties = mutableMapOf(
                    "origin_mission" to originMissionDescription,
                    "change_count" to _dailyMission.value.changeCount
                )
            )
        }.onFailure {
            _eventChannel.send(MissionEvent.ShowSnackbar("일일 미션 변경횟수를 초과했습니다."))
            errorHelper.logError(it)
        }
    }

    sealed class MissionEvent {
        data class ShowSnackbar(val message: String) : MissionEvent()
    }
}
