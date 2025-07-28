package com.virtuous.home.graph.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.virtuous.common_ui.util.clickable
import com.virtuous.designsystem.R
import com.virtuous.designsystem.component.DropdownMenuItem
import com.virtuous.designsystem.component.PostFeed
import com.virtuous.designsystem.component.TraceDropDownMenu
import com.virtuous.designsystem.theme.GrayLine
import com.virtuous.designsystem.theme.PrimaryDefault
import com.virtuous.designsystem.theme.TraceTheme
import com.virtuous.designsystem.theme.White
import com.virtuous.domain.model.post.HomeTab
import com.virtuous.domain.model.post.PostFeed
import com.virtuous.domain.model.post.PostType
import com.virtuous.home.graph.home.HomeViewModel.HomeEvent
import com.virtuous.home.graph.home.component.TabSelector
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.time.LocalDateTime


@Composable
internal fun HomeRoute(
    navigateToSearch: () -> Unit,
    navigateToPost: (PostFeed) -> Unit,
    navigateToWritePost: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val postFeeds = viewModel.postFeeds.collectAsLazyPagingItems()
    val tabType by viewModel.tabType.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.eventChannel.collect { event ->
            when (event) {
                is HomeEvent.NavigateToPost -> navigateToPost(event.postFeed)
                is HomeEvent.NavigateToWritePost -> navigateToWritePost()
                is HomeEvent.NavigateToSearch -> navigateToSearch()
            }
        }
    }

    HomeScreen(
        postFeeds = postFeeds,
        tabType = tabType,
        onTabTypeChange = viewModel::setTabType,
        navigateToPost = { postFeed -> viewModel.onEvent(HomeEvent.NavigateToPost(postFeed)) },
        navigateToWritePost = { viewModel.onEvent(HomeEvent.NavigateToWritePost) },
        navigateToSearch = { viewModel.onEvent(HomeEvent.NavigateToSearch) }
    )
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun HomeScreen(
    postFeeds: LazyPagingItems<PostFeed>,
    tabType: HomeTab,
    onTabTypeChange: (HomeTab) -> Unit,
    navigateToSearch: () -> Unit,
    navigateToPost: (PostFeed) -> Unit,
    navigateToWritePost: () -> Unit,
) {
    var isHomeDropDownMenuExpanded by remember { mutableStateOf(false) }

    val isRefreshing = postFeeds.loadState.refresh is LoadState.Loading
    val isAppending = postFeeds.loadState.append is LoadState.Loading

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { postFeeds.refresh() }
    )
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 105.dp, start = 20.dp, end = 20.dp)
        ) {
            items(postFeeds.itemCount) { index ->
                postFeeds[index]?.let {
                    PostFeed(
                        postFeed = it,
                        navigateToPost = navigateToPost
                    )

                    Spacer(Modifier.height(8.dp))

                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth(),
                        thickness = 1.dp,
                        color = GrayLine
                    )
                }

                Spacer(Modifier.height(15.dp))
            }
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            contentColor = PrimaryDefault,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        Column(
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        PrimaryDefault
                    )
                    .padding(horizontal = 20.dp)
                    .height(45.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "흔적들",
                    style = TraceTheme.typography.headingMB,
                    color = White,
                    modifier = Modifier.height(24.dp)
                )

                Spacer(Modifier.weight(1f))

                Image(
                    painter = painterResource(R.drawable.search_ic),
                    contentDescription = "검색",
                    modifier = Modifier.clickable {
                        navigateToSearch()
                    })

                Spacer(Modifier.width(35.dp))

                Box() {
                    Image(
                        painter = painterResource(R.drawable.menu_ic),
                        contentDescription = "메뉴",
                        modifier = Modifier.clickable {
                            isHomeDropDownMenuExpanded = true
                        })

                    TraceDropDownMenu(
                        expanded = isHomeDropDownMenuExpanded,
                        onDismiss = { isHomeDropDownMenuExpanded = false },
                        items = listOf(
                            DropdownMenuItem(
                                imageVector = Icons.Outlined.Refresh,
                                labelRes = R.string.refresh,
                                action = {
                                    postFeeds.refresh()
                                    coroutineScope.launch {
                                        listState.scrollToItem(0)
                                    }
                                }
                            ),
                            DropdownMenuItem(
                                imageVector = Icons.Outlined.Edit,
                                labelRes = R.string.write_post,
                                action = { navigateToWritePost() }
                            )
                        )
                    )
                }

            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .size(50.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    HomeTab.entries.forEachIndexed { index, type ->
                        TabSelector(
                            type = type,
                            selectedType = tabType,
                            onTabSelected = onTabTypeChange
                        )

                        if (index != HomeTab.entries.size - 1) Spacer(Modifier.width(12.dp))
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = navigateToWritePost,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 25.dp, end = 16.dp)
                .shadow(8.dp, shape = CircleShape),
            containerColor = PrimaryDefault,
            contentColor = White,
            shape = CircleShape,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.write_ic),
                contentDescription = "게시글 쓰기",
            )
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


@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        tabType = HomeTab.ALL,
        onTabTypeChange = {},
        navigateToPost = {},
        navigateToWritePost = {},
        navigateToSearch = {},
        postFeeds = fakeLazyPagingPosts()
    )
}

@Composable
internal fun fakeLazyPagingPosts(): LazyPagingItems<PostFeed> {
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


