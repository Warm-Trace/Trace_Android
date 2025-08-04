package com.virtuous.home.graph.notification.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.virtuous.common_ui.util.clickable
import com.virtuous.designsystem.R
import com.virtuous.designsystem.component.TraceDropDownMenu
import com.virtuous.designsystem.component.TraceDropdownMenuItem
import com.virtuous.designsystem.theme.Gray
import com.virtuous.designsystem.theme.PrimaryDefault
import com.virtuous.designsystem.theme.TraceTheme
import com.virtuous.designsystem.theme.WarmGray
import com.virtuous.domain.model.notification.Notification
import com.virtuous.domain.model.notification.NotificationType
import com.virtuous.domain.model.post.Emotion.GRATEFUL
import com.virtuous.domain.model.post.Emotion.HEARTWARMING
import com.virtuous.domain.model.post.Emotion.IMPRESSIVE
import com.virtuous.domain.model.post.Emotion.LIKEABLE
import com.virtuous.domain.model.post.Emotion.TOUCHING
import java.time.LocalDateTime

@Composable
internal fun NotificationView(
    notification: Notification,
    navigateToPost: (Int) -> Unit,
    readNotification: (String) -> Unit,
    deleteNotification: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showNotiMenu by remember { mutableStateOf(false) }

    val icon = with(notification) {
        when (type) {
            NotificationType.COMMENT -> R.drawable.comment_ic

            NotificationType.EMOTION -> when (emotion) {
                HEARTWARMING -> R.drawable.heartwarming
                LIKEABLE -> R.drawable.likeable
                TOUCHING -> R.drawable.touching
                IMPRESSIVE -> R.drawable.impressive
                GRATEFUL -> R.drawable.grateful
                else -> R.drawable.heartwarming
            }

            NotificationType.MISSION -> R.drawable.mission
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = notification.postId != null) {
                notification.postId?.let {
                    navigateToPost(notification.postId!!)
                    readNotification(notification.id)
                }
            }
    ) {
        if (!notification.isRead) {
            Canvas(modifier = Modifier.padding(top = 12.dp).size(6.dp)) {
                drawCircle(
                    color = PrimaryDefault
                )
            }

            Spacer(Modifier.width(10.dp))
        }
        else {
            Spacer(Modifier.width(16.dp))
        }

        Image(
            painter = painterResource(icon),
            contentDescription = "알림 아이콘",
            modifier = Modifier.size(28.dp),
        )

        Spacer(Modifier.width(12.dp))

        Column {
            Text(
                notification.title,
                style = TraceTheme.typography.bodyXMB,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(6.dp))

            Text(
                notification.body,
                style = TraceTheme.typography.bodySM,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(5.dp))

            Text(
                notification.formattedCreatedAt,
                style = TraceTheme.typography.bodyXSM,
                color = Gray
            )
        }

        Spacer(Modifier.weight(1f))

        Box() {
            Image(
                painter = painterResource(R.drawable.menu_ic),
                contentDescription = "알림 메뉴",
                colorFilter = ColorFilter.tint(WarmGray),
                modifier = Modifier
                    .height(15.dp)
                    .clickable {
                        showNotiMenu = true
                    })

            TraceDropDownMenu(
                expanded = showNotiMenu,
                onDismiss = { showNotiMenu = false },
                items = listOf(
                    TraceDropdownMenuItem(
                        iconRes = R.drawable.delete_ic,
                        labelRes = R.string.delete,
                        action = {
                            deleteNotification(notification.id)
                        }
                    ),
                )
            )
        }


    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationViewPreview() {
    TraceTheme {
        NotificationView(
            notification = Notification(
                id = "",
                isRead = false,
                createdAt = LocalDateTime.now(),
                postId = 1,
                title = "새로운 댓글이 달렸습니다.",
                body = "정성스러운 글이네요. 잘 읽고 갑니다.",
                type = NotificationType.COMMENT,
            ),
            navigateToPost = {},
            readNotification = {},
            deleteNotification = {}
        )
    }
}