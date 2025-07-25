package com.virtuous.mission.graph.mission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.virtuous.common_ui.event.EventHelper
import com.virtuous.common_ui.event.TraceEvent
import com.virtuous.domain.model.mission.DailyMission
import com.virtuous.domain.model.mission.Mission
import com.virtuous.domain.repository.MissionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MissionViewModel @Inject constructor(
    private val missionRepository: MissionRepository,
    private val eventHelper: com.virtuous.common_ui.event.EventHelper,
) : ViewModel() {
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
        }
    }

    fun changeDailyMission() = viewModelScope.launch {
        missionRepository.changeDailyMission().onSuccess { dailyMission ->
            _dailyMission.value = dailyMission
        }.onFailure {
            eventHelper.sendEvent(com.virtuous.common_ui.event.TraceEvent.ShowSnackBar("일일 미션 변경횟수를 초과했습니다."))
        }
    }
}


