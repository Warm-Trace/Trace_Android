package com.virtuous.mission.graph.mission

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.virtuous.designsystem.theme.PrimaryDefault
import com.virtuous.designsystem.theme.TraceTheme
import com.virtuous.domain.model.mission.DailyMission
import com.virtuous.domain.model.mission.Mission
import com.virtuous.domain.model.mission.MissionFeed
import com.virtuous.mission.graph.mission.component.MissionCompletedHeaderView
import com.virtuous.mission.graph.mission.component.MissionHeaderView
import com.virtuous.mission.graph.mission.component.VerifiedMissionBox
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate

@Composable
internal fun MissionRoute(
    viewModel: MissionViewModel = hiltViewModel(),
    navigateToPost: (Int) -> Unit,
    navigateToVerifyMission: (String) -> Unit,
) {
    val dailyMission by viewModel.dailyMission.collectAsStateWithLifecycle()
    val completedMissions = viewModel.completedMissions.collectAsLazyPagingItems()

    if (!dailyMission.mission.isVerified) {
        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    viewModel.getDailyMission()
                    completedMissions.refresh()
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    }

    MissionScreen(
        dailyMission = dailyMission,
        completedMissions = completedMissions,
        changeMission = viewModel::changeDailyMission,
        navigateToPost = navigateToPost,
        onVerifyMission = navigateToVerifyMission
    )
}

@Composable
private fun MissionScreen(
    dailyMission: DailyMission,
    completedMissions: LazyPagingItems<MissionFeed>,
    changeMission: () -> Unit,
    navigateToPost: (Int) -> Unit,
    onVerifyMission: (String) -> Unit,
) {
    val isRefreshing = completedMissions.loadState.refresh is LoadState.Loading
    val isAppending = completedMissions.loadState.append is LoadState.Loading

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 15.dp, horizontal = 20.dp)
    ) {
        item {
            if (!dailyMission.mission.isVerified) {
                MissionHeaderView(
                    dailyMission = dailyMission,
                    changeMission = changeMission,
                    verifyMission = onVerifyMission
                )
            }

            if (dailyMission.mission.isVerified) {
                MissionCompletedHeaderView(
                    dailyMission = dailyMission
                )
            }

            Spacer(Modifier.height(16.dp))
        }

        item {
            Text(
                "미션 기록",
                style = TraceTheme.typography.bodySSB.copy(fontSize = 16.sp, lineHeight = 20.sp)
            )

            Spacer(Modifier.height(16.dp))
        }

        items(count = completedMissions.itemCount) { index ->
            Box {
                Column {
                    completedMissions[index]?.let {
                        VerifiedMissionBox(it, navigateToPost = navigateToPost)

                        Spacer(Modifier.height(10.dp))
                    }
                }

                if (isRefreshing || isAppending) {
                    CircularProgressIndicator(
                        color = PrimaryDefault, modifier = Modifier.align(
                            if (isRefreshing) Alignment.Center else Alignment.BottomCenter
                        )
                    )
                }
            }

        }
    }
}


@Preview
@Composable
private fun MissionScreenPreview() {
    MissionScreen(
        dailyMission = DailyMission(
            mission = Mission(
                description = "길거리에서 쓰레기 줍기",
                isVerified = false,
            ),
            changeCount = 0
        ),
        completedMissions = fakeLazyPagingMissions(),
        changeMission = {},
        navigateToPost = {},
        onVerifyMission = {}
    )
}

@Composable
private fun fakeLazyPagingMissions(): LazyPagingItems<MissionFeed> {
    return flowOf(
        PagingData.from(
            listOf(
                MissionFeed(
                    missionId = 1,
                    description = "지하철에서 어르신에게 자리 양보하기",
                    isVerified = true,
                    imageUrl = "https://picsum.photos/200/300?random=1",
                    createdAt = LocalDate.of(2025, 5, 22)
                ),
                MissionFeed(
                    missionId = 2,
                    description = "길거리 쓰레기 줍기",
                    isVerified = false,
                    imageUrl = null,
                    createdAt = LocalDate.of(2025, 5, 21)
                ),
                MissionFeed(
                    missionId = 3,
                    description = "카페에서 다 쓴 컵 정리하기",
                    isVerified = true,
                    imageUrl = "https://picsum.photos/200/300?random=6",
                    createdAt = LocalDate.of(2025, 5, 20)
                ),
                MissionFeed(
                    missionId = 4,
                    description = "지인에게 따뜻한 말 한마디 전하기",
                    isVerified = false,
                    imageUrl = null,
                    createdAt = LocalDate.of(2025, 5, 19)
                ),
                MissionFeed(
                    missionId = 5,
                    description = "엘리베이터 버튼 대신 눌러주기",
                    isVerified = true,
                    imageUrl = "https://picsum.photos/200/300?random=2",
                    createdAt = LocalDate.of(2025, 5, 18)
                )
            )
        )
    ).collectAsLazyPagingItems()
}
