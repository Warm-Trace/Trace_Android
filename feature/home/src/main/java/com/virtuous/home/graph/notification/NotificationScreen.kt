package com.virtuous.home.graph.notification

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.virtuous.common_ui.util.rememberLazyListState
import com.virtuous.designsystem.R
import com.virtuous.designsystem.theme.PrimaryDefault
import com.virtuous.designsystem.theme.TraceTheme
import com.virtuous.domain.model.notification.Notification
import com.virtuous.domain.model.notification.NotificationType
import com.virtuous.domain.model.post.Emotion
import com.virtuous.home.graph.notification.component.NotificationView
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime


@Composable
internal fun NotificationRoute(
    navigateBack: () -> Unit,
    navigateToPost: (Int) -> Unit,
    navigateToMission: () -> Unit,
    viewModel: NotificationViewModel = hiltViewModel(),
) {
    val notifications = viewModel.notifications.collectAsLazyPagingItems()

    NotificationScreen(
        notifications = notifications,
        onRefresh = viewModel::onRefresh,
        readNotification = viewModel::readNotification,
        deleteNotification = viewModel::deleteNotification,
        navigateToPost = navigateToPost,
        navigateBack = navigateBack,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun NotificationScreen(
    notifications: LazyPagingItems<Notification>,
    onRefresh: () -> Unit,
    readNotification: (String) -> Unit,
    deleteNotification: (String) -> Unit,
    navigateToPost: (Int) -> Unit,
    navigateBack: () -> Unit,
) {
    val isRefreshing = notifications.loadState.refresh is LoadState.Loading
    val isAppending = notifications.loadState.append is LoadState.Loading
    val listState = notifications.rememberLazyListState()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            notifications.refresh()
            onRefresh()
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 52.dp, start = 10.dp, end = 20.dp)
        ) {
            item {
                Spacer(Modifier.height(15.dp))
            }

            items(
                count = notifications.itemCount,
                key = { index -> notifications[index]?.id ?: index }
            ) { index ->
                notifications[index]?.let {
                    NotificationView(
                        notification = it,
                        navigateToPost = navigateToPost,
                        readNotification = readNotification,
                        deleteNotification = deleteNotification,
                        modifier = Modifier.animateItem(
                            fadeOutSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessVeryLow
                            )
                        )
                    )
                }

                Spacer(Modifier.height(20.dp))
            }

            item {
                Spacer(Modifier.height(50.dp))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navigateBack() },
                modifier = Modifier
                    .padding(start = 10.dp, top = 2.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_left_ic),
                    contentDescription = "뒤로가기",
                    modifier = Modifier.size(36.dp),
                )
            }

            Spacer(Modifier.width(10.dp))

            Text("알림", style = TraceTheme.typography.bodyMSB)
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            contentColor = PrimaryDefault,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        if (isRefreshing || isAppending) {
            CircularProgressIndicator(
                color = PrimaryDefault, modifier = Modifier.align(
                    if (isRefreshing) Alignment.Center else Alignment.BottomCenter
                )
            )
        }
    }
}

@Preview
@Composable
private fun NotificationScreenPreview() {
    NotificationScreen(
        notifications = fakeLazyPagingNotifications(),
        navigateBack = {},
        navigateToPost = {},
        readNotification = {},
        deleteNotification = {},
        onRefresh = {}
    )
}

@Composable
private fun fakeLazyPagingNotifications(): LazyPagingItems<Notification> {
    val fakeNotifications = listOf(
        Notification(
            id = "",
            isRead = false,
            createdAt = LocalDateTime.now().minusMinutes(5),
            title = "깨끗한 공원 만들기",
            body = "공원을 깨끗하게 만드는 봉사활동입니다.",
            type = NotificationType.EMOTION,
            postId = 1,
            emotion = Emotion.GRATEFUL
        ),
        Notification(
            id = "",
            isRead = false,
            createdAt = LocalDateTime.now().minusHours(1),
            title = "무료 식사 제공",
            body = "어려운 이웃에게 무료 식사를 제공합니다.",
            type = NotificationType.COMMENT,
            postId = 2,
            emotion = Emotion.LIKEABLE
        ),
        Notification(
            id = "",
            isRead = true,
            createdAt = LocalDateTime.now().minusDays(1),
            title = "헌혈 참여",
            body = "생명을 살리는 헌혈에 동참해주세요.",
            type = NotificationType.MISSION,
            postId = 3,
            emotion = Emotion.LIKEABLE
        )
    )
    return flowOf(PagingData.from(fakeNotifications)).collectAsLazyPagingItems()
}