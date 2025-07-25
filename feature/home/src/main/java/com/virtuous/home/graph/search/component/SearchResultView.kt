package com.virtuous.home.graph.search.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.virtuous.common_ui.util.clickable
import com.virtuous.designsystem.component.PostFeed
import com.virtuous.designsystem.theme.Background
import com.virtuous.designsystem.theme.Black
import com.virtuous.designsystem.theme.Gray
import com.virtuous.designsystem.theme.GrayLine
import com.virtuous.designsystem.theme.PrimaryDefault
import com.virtuous.designsystem.theme.TabIndicator
import com.virtuous.designsystem.theme.TraceTheme
import com.virtuous.domain.model.post.PostFeed
import com.virtuous.domain.model.search.SearchTab
import com.virtuous.domain.model.search.SearchType


@Composable
internal fun SearchResultView(
    searchType: SearchType,
    tabType: SearchTab,
    displayedPosts: LazyPagingItems<PostFeed>,
    onSearchTypeChange: (SearchType) -> Unit,
    onTabTypeChange: (SearchTab) -> Unit,
    navigateToPost: (PostFeed) -> Unit,
) {
    val tabs = SearchType.entries

    var isSearchTypeDropDownMenuExpanded by remember { mutableStateOf(false) }

    val isRefreshing = displayedPosts.loadState.refresh is LoadState.Loading
    val isAppending = displayedPosts.loadState.append is LoadState.Loading

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                Spacer(Modifier.height(20.dp))

                Box() {
                    Row(
                        modifier = Modifier
                            .wrapContentWidth()
                            .clickable {
                                isSearchTypeDropDownMenuExpanded = true
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(tabType.label, style = TraceTheme.typography.bodySSB)

                        Spacer(Modifier.width(5.dp))

                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "드롭다운 아이콘"
                        )
                    }

                    TabTypeDropdownMenu(
                        expanded = isSearchTypeDropDownMenuExpanded,
                        onTabTypeChange = onTabTypeChange,
                        onDismiss = { isSearchTypeDropDownMenuExpanded = false },
                        selectedTabType = tabType
                    )
                }

                Spacer(Modifier.height(40.dp))

                TabRow(
                    selectedTabIndex = tabs.indexOf(searchType),
                    containerColor = Background,
                    contentColor = Black,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            color = TabIndicator,
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[tabs.indexOf(searchType)])
                        )
                    }
                ) {
                    tabs.forEach { tab ->
                        Tab(
                            selected = tab == searchType,
                            onClick = { onSearchTypeChange(tab) },
                            text = {
                                Text(
                                    tab.label,
                                    style = TraceTheme.typography.bodyXMR
                                )
                            }
                        )
                    }
                }

                Spacer(Modifier.height(15.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${displayedPosts.itemCount}개", style = TraceTheme.typography.bodySR)

                    Spacer(Modifier.weight(1f))

//            Row(
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Icon(
//                    painter = painterResource(R.drawable.sort_ic),
//                    contentDescription = "정렬",
//                    tint = LightGray
//                )
//
//                Spacer(Modifier.width(5.dp))
//
//                Text("정렬", style = TraceTheme.typography.bodySM, color = LightGray)
//            }
                }

                Spacer(Modifier.height(25.dp))

                if (displayedPosts.itemCount == 0) {

                    Spacer(Modifier.height(100.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "검색 결과가 없습니다.",
                            style = TraceTheme.typography.bodySM,
                            color = Gray,
                            modifier = Modifier.align(
                                Alignment.Center
                            )
                        )
                    }

                    Spacer(Modifier.height(3.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "다른 검색어를 입력하거나 필터를 다시 선택해 주세요.",
                            style = TraceTheme.typography.bodySM,
                            color = Gray,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }

            items(displayedPosts.itemCount) { index ->
                displayedPosts[index]?.let { postFeed ->
                    PostFeed(postFeed, navigateToPost = { navigateToPost(postFeed) })

                    Spacer(Modifier.height(8.dp))

                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(GrayLine)
                    )

                    Spacer(Modifier.height(15.dp))

                }
            }

            item {
                Spacer(Modifier.height(200.dp))
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
