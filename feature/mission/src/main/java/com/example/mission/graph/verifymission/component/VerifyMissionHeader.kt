package com.example.mission.graph.verifymission.component

import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.designsystem.theme.Black
import com.example.designsystem.theme.MissionBackground
import com.example.designsystem.theme.MissionHeader
import com.example.designsystem.theme.TraceTheme

@Composable
internal fun VerifyMissionHeaderView(
    description: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(16.dp)
            )
            .background(MissionBackground)
            .padding(top = 25.dp, bottom = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
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

        Spacer(Modifier.height(10.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                description,
                style = TraceTheme.typography.missionTitle,
                color = Black,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

