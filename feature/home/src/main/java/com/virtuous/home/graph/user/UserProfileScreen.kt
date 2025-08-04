package com.virtuous.home.graph.user

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.virtuous.designsystem.component.PostFeed
import com.virtuous.designsystem.component.ProfileImage
import com.virtuous.designsystem.theme.Background
import com.virtuous.designsystem.theme.Black
import com.virtuous.designsystem.theme.GrayLine
import com.virtuous.designsystem.theme.PrimaryDefault
import com.virtuous.designsystem.theme.TabIndicator
import com.virtuous.designsystem.theme.TraceTheme
import com.virtuous.domain.model.home.UserProfileTab
import com.virtuous.domain.model.post.PostFeed
import com.virtuous.domain.model.post.PostType
import com.virtuous.domain.model.user.UserInfo
import com.virtuous.home.graph.user.UserProfileViewModel.UserProfileEvent
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime

@Composable
internal fun UserProfileRoute(
    navigateBack: () -> Unit,
    navigateToPost: (PostFeed) -> Unit,
    viewModel: UserProfileViewModel = hiltViewModel()
) {
    val userInfo by viewModel.userInfo.collectAsStateWithLifecycle()
    val tabType by viewModel.tabType.collectAsStateWithLifecycle()
    val displayedPosts = viewModel.displayedPosts.collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        viewModel.eventChannel.collect { event ->
            when (event) {
                is UserProfileEvent.NavigateBack -> navigateBack()
                is UserProfileEvent.NavigateToPost -> navigateToPost(event.postFeed)
            }
        }
    }

    UserProfileScreen(
        userInfo = userInfo,
        tabType = tabType,
        displayedPosts = displayedPosts,
        onTabTypeChange = viewModel::setTabType,
        navigateToPost = { postFeed -> viewModel.onEvent(UserProfileEvent.NavigateToPost(postFeed)) },
        navigateBack = navigateBack
    )
}

@Composable
private fun UserProfileScreen(
    userInfo: UserInfo,
    tabType: UserProfileTab,
    displayedPosts: LazyPagingItems<PostFeed>,
    onTabTypeChange: (UserProfileTab) -> Unit,
    navigateToPost: (PostFeed) -> Unit,
    navigateBack: () -> Unit
) {
    val tabs = UserProfileTab.entries
    val isRefreshing = displayedPosts.loadState.refresh is LoadState.Loading
    val isAppending = displayedPosts.loadState.append is LoadState.Loading

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 42.dp, start = 20.dp, end = 14.dp
                )
        ) {
            item {
                Spacer(Modifier.height(28.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileImage(
                        profileImageUrl = userInfo.profileImageUrl,
                        imageSize = if (userInfo.profileImageUrl != null) 96.dp else 86.dp,
                        paddingValue = if (userInfo.profileImageUrl != null) 2.dp else 7.dp,
                        strokeWidth = 10f,
                    )

                    Spacer(Modifier.width(20.dp))

                    Column {
                        Text(userInfo.name, style = TraceTheme.typography.headingLB)


                        Spacer(Modifier.height(10.dp))

                        Text(
                            "선행 점수 ${userInfo.verificationScore}",
                            style = TraceTheme.typography.bodyMR.copy(
                                fontSize = 15.sp,
                                lineHeight = 19.sp
                            )
                        )

                        Spacer(Modifier.height(5.dp))

                        Text(
                            "선행 마크 ${userInfo.verificationCount}",
                            style = TraceTheme.typography.bodyMR.copy(
                                fontSize = 15.sp,
                                lineHeight = 19.sp
                            )
                        )
                    }
                }

                Spacer(Modifier.height(28.dp))

                TabRow(
                    selectedTabIndex = tabs.indexOf(tabType),
                    containerColor = Background,
                    contentColor = Black,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            color = TabIndicator,
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[tabs.indexOf(tabType)])
                        )
                    }
                ) {
                    tabs.forEach { tab ->
                        Tab(
                            selected = tab == tabType,
                            onClick = { onTabTypeChange(tab) },
                            text = {
                                Text(
                                    tab.label,
                                    style = TraceTheme.typography.myPageTab
                                )
                            }
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))
            }

            items(displayedPosts.itemCount) { index ->
                Box {
                    Column {
                        displayedPosts[index]?.let { postFeed ->
                            PostFeed(postFeed, navigateToPost = { navigateToPost(postFeed) })

                            Spacer(Modifier.height(8.dp))

                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                thickness = 1.dp,
                                color = GrayLine
                            )

                            Spacer(Modifier.height(15.dp))
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(50.dp))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { navigateBack() },
                modifier = Modifier
                    .padding(start = 10.dp, top = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                    contentDescription = "뒤로가기",
                    modifier = Modifier.size(36.dp),
                )
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

@Preview(showBackground = true)
@Composable
private fun UserProfileScreenPreview() {
    UserProfileScreen(
        userInfo = UserInfo("닉네임", null, 0, 0),
        tabType = UserProfileTab.WRITTEN_POSTS,
        displayedPosts = fakeLazyPagingPosts(),
        onTabTypeChange = {},
        navigateToPost = {},
        navigateBack = {}
    )
}

@Composable
private fun fakeLazyPagingPosts(): LazyPagingItems<PostFeed> {
    return flowOf(
        PagingData.from(
            listOf(
                PostFeed(
                    postType = PostType.GOOD_DEED,
                    title = "깨끗한 공원 만들기",
                    content = "오늘 공원에서 쓰레기를 줍고 깨끗한 환경을 만들었습니다. 주변 사람들이 함께 참여해주셨습니다.",
                    nickname = "선행자1",
                    createdAt = LocalDateTime.now(),
                    viewCount = 150,
                    commentCount = 5,
                    isVerified = true,
                    postId = 1, providerId = "1234", updatedAt = LocalDateTime.now()
                ),
                PostFeed(
                    postType = PostType.GOOD_DEED,
                    title = "무료 식사 제공",
                    content = "어려운 이웃을 위해 무료로 식사를 제공했습니다. 작은 도움이지만 큰 의미가 있었습니다.",
                    nickname = "선행자2",
                    createdAt = LocalDateTime.now().minusMinutes(30),
                    viewCount = 220,
                    commentCount = 10,
                    isVerified = false,
                    imageUrl = "https://picsum.photos/200/300?random=2",
                    postId = 2,
                    providerId = "1234", updatedAt = LocalDateTime.now()
                ),
                PostFeed(
                    postType = PostType.GOOD_DEED,
                    title = "헌혈 참여",
                    content = "지역 헌혈 행사에 참여하여 기부하였습니다. 많은 분들이 참여해주셔서 좋았습니다.",
                    nickname = "선행자3",
                    createdAt = LocalDateTime.now().minusMinutes(50),
                    viewCount = 300,
                    commentCount = 8,
                    isVerified = true,
                    postId = 2,
                    providerId = "1234", updatedAt = LocalDateTime.now()
                ),
                PostFeed(
                    postType = PostType.GOOD_DEED,
                    title = "무료 도서 기부",
                    content = "사용하지 않는 책을 기부하여 많은 사람들이 혜택을 볼 수 있게 했습니다.",
                    nickname = "선행자4",
                    createdAt = LocalDateTime.now().minusHours(1),
                    viewCount = 175,
                    commentCount = 12,
                    isVerified = true,
                    imageUrl = "https://picsum.photos/200/300?random=4",
                    postId = 1,
                    providerId = "1234", updatedAt = LocalDateTime.now()
                ),
                PostFeed(
                    postType = PostType.GOOD_DEED,
                    title = "환경 보호 캠페인",
                    content = "자연을 보호하는 캠페인에 참여했습니다. 지구를 위한 작은 노력!",
                    nickname = "선행자5",
                    createdAt = LocalDateTime.now().minusHours(3),
                    viewCount = 500,
                    commentCount = 35,
                    isVerified = false,
                    postId = 1,
                    providerId = "1234", updatedAt = LocalDateTime.now()
                ),
                PostFeed(
                    postType = PostType.GOOD_DEED,
                    title = "기부금 모금 활동",
                    content = "소외된 이웃을 돕기 위해 기부금을 모금하였습니다.",
                    nickname = "선행자6",
                    createdAt = LocalDateTime.now().minusDays(6),
                    viewCount = 400,
                    commentCount = 28,
                    isVerified = true,
                    imageUrl = "https://picsum.photos/200/300?random=6",
                    postId = 1, providerId = "1234", updatedAt = LocalDateTime.now()
                ),
                PostFeed(
                    postType = PostType.GOOD_DEED,
                    title = "청소년 멘토링 활동",
                    content = "청소년들에게 멘토링을 통해 더 나은 미래를 꿈꾸도록 도왔습니다.",
                    nickname = "선행자7",
                    createdAt = LocalDateTime.now().minusDays(3),
                    viewCount = 320,
                    commentCount = 15,
                    isVerified = true,
                    postId = 1, providerId = "1234", updatedAt = LocalDateTime.now()
                ),
                PostFeed(
                    postType = PostType.GOOD_DEED,
                    title = "재활용 캠페인",
                    content = "재활용을 촉진하는 캠페인에 참여해 재활용 활동을 지원했습니다.",
                    nickname = "선행자8",
                    createdAt = LocalDateTime.now().minusDays(4),
                    viewCount = 220,
                    commentCount = 18,
                    isVerified = false,
                    imageUrl = "https://picsum.photos/200/300?random=8",
                    postId = 1, providerId = "1234", updatedAt = LocalDateTime.now()
                ),
                PostFeed(
                    postType = PostType.GOOD_DEED,
                    title = "노숙인들에게 의류 기부",
                    content = "기부한 옷이 많은 노숙인들에게 도움이 되었길 바랍니다.",
                    nickname = "선행자9",
                    createdAt = LocalDateTime.now().minusDays(6),
                    viewCount = 250,
                    commentCount = 13,
                    isVerified = false,
                    imageUrl = "https://picsum.photos/200/300?random=9",
                    postId = 1, providerId = "1234", updatedAt = LocalDateTime.now()
                ),
                PostFeed(
                    postType = PostType.GOOD_DEED,
                    title = "아름다운 거리 만들기",
                    content = "동네에서 거리 청소와 아름다운 꽃밭을 조성했습니다.",
                    nickname = "선행자10",
                    createdAt = LocalDateTime.now().minusDays(8),
                    viewCount = 100,
                    commentCount = 5,
                    isVerified = true,
                    imageUrl = "https://picsum.photos/200/300?random=10",
                    postId = 1, providerId = "1234", updatedAt = LocalDateTime.now()
                )
            )
        )
    ).collectAsLazyPagingItems()
}