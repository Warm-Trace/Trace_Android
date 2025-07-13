package com.virtuous.mission.graph.mission.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.virtuous.common.util.clickable
import com.virtuous.designsystem.theme.Background
import com.virtuous.designsystem.theme.TraceTheme
import com.virtuous.domain.model.mission.MissionFeed
import java.time.LocalDate

@Composable
internal fun VerifiedMissionBox(
    missionFeed: MissionFeed,
    navigateToPost: (Int) -> Unit,
) {
    if (missionFeed.imageUrl != null) {
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(missionFeed.imageUrl)
                .crossfade(true)
                .build()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp), clip = true)
                .background(Background)
                .padding(horizontal = 20.dp, vertical = 15.dp)
                .clickable {
                    navigateToPost(missionFeed.missionId)
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 85.dp)
                    .align(Alignment.CenterStart)
            ) {
                Text(
                    missionFeed.getFormattedDate(),
                    style = TraceTheme.typography.bodyMM.copy(fontSize = 16.sp, lineHeight = 20.sp)
                )

                Spacer(Modifier.height(5.dp))

                Text(
                    missionFeed.description,
                    style = TraceTheme.typography.bodySR,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }


            Box(
                modifier = Modifier
                    .size(45.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .align(Alignment.CenterEnd)
            ) {
                Image(
                    painter = painter,
                    contentDescription = "대표 이미지",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp), clip = true)
                .background(Background)
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .clickable {
                    navigateToPost(missionFeed.missionId)
                }
        ) {
            Text(
                missionFeed.getFormattedDate(),
                style = TraceTheme.typography.bodyMM.copy(fontSize = 16.sp, lineHeight = 20.sp)
            )

            Spacer(Modifier.height(5.dp))

            Text(
                missionFeed.description,
                style = TraceTheme.typography.bodySR,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

    }
}

@Preview
@Composable
fun VerifiedMissionBoxPreview() {
    Column(Modifier.fillMaxSize()) {
        VerifiedMissionBox(
            missionFeed = MissionFeed(
                missionId = 3,
                description = "카페에서 다 쓴 컵 정리하기",
                isVerified = true,
                imageUrl = "https://picsum.photos/200/300?random=6",
                createdAt = LocalDate.of(2025, 5, 20,)
            ),
            navigateToPost = {}
        )
    }


}