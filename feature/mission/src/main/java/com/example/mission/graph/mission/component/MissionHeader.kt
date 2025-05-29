package com.example.mission.graph.mission.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.common.util.clickable
import com.example.designsystem.R
import com.example.designsystem.theme.Black
import com.example.designsystem.theme.MissionBackground
import com.example.designsystem.theme.MissionCompletedBackground
import com.example.designsystem.theme.MissionHeader
import com.example.designsystem.theme.TraceTheme
import com.example.designsystem.theme.VerificationButton
import com.example.designsystem.theme.White
import com.example.domain.model.mission.DailyMission
import com.example.domain.model.mission.MAX_MISSION_CHANGE_COUNT

@Composable
internal fun MissionHeaderView(
    dailyMission: DailyMission,
    changeMission: () -> Unit,
    verifyMission: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(16.dp)
            )
            .background(MissionBackground)
            .padding(start = 20.dp, end = 12.dp, top = 4.dp)

    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Canvas(modifier = Modifier.size(6.dp)) {
                    drawCircle(
                        color = MissionHeader,
                        radius = size.minDimension / 2f
                    )
                }

                Spacer(Modifier.width(4.dp))

                Text(
                    "오늘의 선행 미션",
                    style = TraceTheme.typography.missionHeader,
                    color = MissionHeader
                )

                Spacer(Modifier.width(4.dp))

                Canvas(modifier = Modifier.size(6.dp)) {
                    drawCircle(
                        color = MissionHeader,
                        radius = size.minDimension / 2f
                    )
                }

            }

            Box(
                modifier = Modifier.align(Alignment.TopEnd),
            ) {
                IconButton(
                    onClick = { changeMission() },
                    content = {
                        Image(
                            painter = painterResource(R.drawable.refresh_mission_ic),
                            contentDescription = "미션 변경",
                        )
                    },
                    modifier = Modifier.align(Alignment.TopCenter)
                )

                Text(
                    "${dailyMission.changeCount}/${MAX_MISSION_CHANGE_COUNT}",
                    style = TraceTheme.typography.bodySR.copy(
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    ),
                    color = MissionHeader,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(top = 1.dp)
                )
            }
        }

        Spacer(Modifier.height(38.dp))

        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 25.dp)) {
            Text(
                dailyMission.mission.description,
                style = TraceTheme.typography.missionTitle,
                color = Black,
            )
        }

        Spacer(Modifier.height(35.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(VerificationButton)
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .clickable {
                        verifyMission(dailyMission.mission.description)
                    }
            ) {
                Text(
                    "인증하기",
                    style = TraceTheme.typography.missionVerification,
                    color = White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Spacer(Modifier.height(15.dp))
    }
}


@Composable
internal fun MissionCompletedHeaderView(
    dailyMission: DailyMission,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(16.dp)
            )
            .background(MissionCompletedBackground)
            .padding(start = 20.dp, end = 20.dp, top = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Canvas(modifier = Modifier.size(6.dp)) {
                drawCircle(
                    color = MissionHeader,
                    radius = size.minDimension / 2f
                )
            }

            Spacer(Modifier.width(4.dp))

            Text(
                "오늘의 선행 미션",
                style = TraceTheme.typography.missionHeader,
                color = MissionHeader
            )

            Spacer(Modifier.width(4.dp))

            Canvas(modifier = Modifier.size(6.dp)) {
                drawCircle(
                    color = MissionHeader,
                    radius = size.minDimension / 2f
                )
            }
        }

        Spacer(Modifier.height(15.dp))

        Image(
            painter = painterResource(R.drawable.mission_comleted_text),
            contentDescription = "미션 완료"
        )

        Spacer(Modifier.height(25.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                dailyMission.mission.description,
                style = TraceTheme.typography.missionCompletedTitle,
                color = Black,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(Modifier.height(35.dp))

    }
}