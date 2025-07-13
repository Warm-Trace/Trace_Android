package com.virtuous.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.virtuous.common.util.clickable
import com.virtuous.designsystem.R
import com.virtuous.designsystem.theme.DarkGray
import com.virtuous.designsystem.theme.PrimaryDefault
import com.virtuous.designsystem.theme.Red
import com.virtuous.designsystem.theme.TraceTheme
import com.virtuous.designsystem.theme.WarmGray
import com.virtuous.domain.model.post.PostFeed
import com.virtuous.domain.model.post.PostType
import java.time.LocalDateTime

@Composable
fun PostFeed(
    postFeed: PostFeed,
    navigateToPost: (PostFeed) -> Unit
) {

    val painter = postFeed.imageUrl?.let {
        rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(it)
                .crossfade(true)
                .build()
        )
    }

    val endPadding = if (postFeed.imageUrl != null) 95.dp else 0.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navigateToPost(postFeed)
            }) {

        Column(
            modifier = Modifier
                .padding(end = endPadding)
                .align(Alignment.CenterStart)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    postFeed.title,
                    style = TraceTheme.typography.bodyMSB.copy(fontSize = 16.sp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (postFeed.isVerified) {
                    Spacer(Modifier.width(4.dp))

                    Image(
                        painter = painterResource(R.drawable.verification_mark),
                        contentDescription = "선행 인증 마크"
                    )
                }
            }

            Spacer(Modifier.height(3.dp))

            Text(
                postFeed.content,
                style = TraceTheme.typography.bodySSB.copy(fontSize = 13.sp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = DarkGray
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileImage(
                    profileImageUrl = postFeed.profileImageUrl,
                    imageSize = if (postFeed.profileImageUrl != null) 18.dp else 16.dp,
                    paddingValue = if (postFeed.profileImageUrl != null) 1.dp else 2.dp
                )

                Spacer(Modifier.width(6.dp))

                Text(
                    postFeed.nickname,
                    style = TraceTheme.typography.bodySSB.copy(fontSize = 11.sp),
                    color = WarmGray
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    postFeed.getFormattedTime(),
                    style = TraceTheme.typography.bodySSB.copy(fontSize = 11.sp),
                    color = WarmGray
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    "${postFeed.viewCount} 읽음",
                    style = TraceTheme.typography.bodySSB.copy(fontSize = 11.sp),
                    color = WarmGray
                )

                Spacer(Modifier.width(7.dp))

                Image(
                    painter = painterResource(R.drawable.comment_ic),
                    contentDescription = "댓글 아이콘",
                    colorFilter = ColorFilter.tint(
                        PrimaryDefault
                    )
                )

                Spacer(Modifier.width(3.dp))

                Text(
                    "${postFeed.commentCount}",
                    style = TraceTheme.typography.bodySSB.copy(fontSize = 11.sp),
                    color = PrimaryDefault
                )

                Spacer(Modifier.width(10.dp))

                Image(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "감정표현",
                    colorFilter = ColorFilter.tint(Red),
                    modifier = Modifier.size(15.dp)
                )

                Spacer(Modifier.width(3.dp))

                Text(
                    postFeed.totalEmotionCount.toString(),
                    style = TraceTheme.typography.bodySSB.copy(fontSize = 11.sp),
                    color = WarmGray
                )
            }

        }

        if (postFeed.imageUrl != null && painter != null) {
            Box(
                modifier = Modifier
                    .size(75.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .align(Alignment.CenterEnd),
            ) {
                Image(
                    painter = painter,
                    contentDescription = "대표 이미지",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

}

@Preview
@Composable
private fun PostFeedPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 15.dp)
    ) {
        PostFeed(
            postType = PostType.GOOD_DEED,
            title = "깨끗한 공원 만들기",
            content = "오늘 공원에서 쓰레기를 줍고 깨끗한 환경을 만들었습니다. 주변 사람들이 함께 참여해주셨습니다.",
            nickname = "선행자1",
            createdAt = LocalDateTime.now(),
            viewCount = 150,
            commentCount = 5,
            isVerified = true,
            postId = 1, providerId = "1234", updatedAt = LocalDateTime.now(),
        )
    }
}
