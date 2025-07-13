package com.virtuous.home.graph.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.virtuous.common.util.clickable
import com.virtuous.designsystem.R
import com.virtuous.designsystem.theme.Background
import com.virtuous.designsystem.theme.PrimaryDefault
import com.virtuous.domain.model.post.PostFeed
import com.virtuous.domain.model.search.SearchTab
import com.virtuous.domain.model.search.SearchType
import com.virtuous.home.graph.home.fakeLazyPagingPosts
import com.virtuous.home.graph.search.SearchViewModel.SearchEvent
import com.virtuous.home.graph.search.component.SearchInitialView
import com.virtuous.home.graph.search.component.SearchResultView
import com.virtuous.home.graph.search.component.TraceSearchField

@Composable
internal fun SearchRoute(
    navigateBack: () -> Unit,
    navigateToPost: (PostFeed) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val keywordInput by viewModel.keywordInput.collectAsStateWithLifecycle()
    val recentKeywords by viewModel.recentKeywords.collectAsStateWithLifecycle()
    val isSearched by viewModel.isSearched.collectAsStateWithLifecycle()
    val searchType by viewModel.searchType.collectAsStateWithLifecycle()
    val tabType by viewModel.tabType.collectAsStateWithLifecycle()
    val displayedPosts = viewModel.postFeeds.collectAsLazyPagingItems()

    LaunchedEffect(isSearched) {
        viewModel.loadRecentKeywords()
    }

    LaunchedEffect(Unit) {
        viewModel.eventChannel.collect { event ->
            when (event) {
                is SearchEvent.NavigateToPost -> navigateToPost(event.postFeed)
                is SearchEvent.NavigateBack -> navigateBack()
            }
        }
    }

    SearchScreen(
        keywordInput = keywordInput,
        recentKeywords = recentKeywords,
        isSearched = isSearched,
        searchType = searchType,
        tabType = tabType,
        displayedPosts = displayedPosts,
        onKeywordInputChange = viewModel::setKeywordInput,
        onSearchTypeChange = viewModel::setSearchType,
        onTabTypeChange = viewModel::setTabType,
        removeKeyword = viewModel::removeKeyword,
        clearKeywords = viewModel::clearKeywords,
        searchByInput = viewModel::searchByInput,
        searchByRecentKeyword = viewModel::searchByRecentKeyword,
        resetSearch = viewModel::resetSearch,
        navigateBack = { viewModel.onEvent(SearchEvent.NavigateBack) },
        navigateToPost = { postFeed -> viewModel.onEvent(SearchEvent.NavigateToPost(postFeed)) }
    )
}

@Composable
private fun SearchScreen(
    keywordInput: String,
    recentKeywords: List<String>,
    isSearched: Boolean,
    searchType: SearchType,
    tabType: SearchTab,
    displayedPosts: LazyPagingItems<PostFeed>,
    onKeywordInputChange: (String) -> Unit,
    onSearchTypeChange: (SearchType) -> Unit,
    onTabTypeChange: (SearchTab) -> Unit,
    searchByInput: () -> Unit,
    searchByRecentKeyword: (String) -> Unit,
    removeKeyword: (String) -> Unit,
    clearKeywords: () -> Unit,
    resetSearch: () -> Unit,
    navigateBack: () -> Unit,
    navigateToPost: (PostFeed) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp, top = 50.dp)
        ) {
            if (!isSearched) {
                SearchInitialView(
                    recentKeywords = recentKeywords,
                    removeKeyword = removeKeyword,
                    clearKeywords = clearKeywords,
                    onSearch = searchByRecentKeyword,
                )
            } else {
                SearchResultView(
                    searchType = searchType,
                    tabType = tabType,
                    onSearchTypeChange = onSearchTypeChange,
                    onTabTypeChange = onTabTypeChange,
                    displayedPosts = displayedPosts,
                    navigateToPost = navigateToPost,
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(
                    PrimaryDefault
                )
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.arrow_back_white_ic),
                contentDescription = "뒤로 가기",
                modifier = Modifier.clickable {
                    navigateBack()
                })

            Spacer(Modifier.width(20.dp))

            TraceSearchField(
                focusRequester = focusRequester,
                value = keywordInput,
                onValueChange = onKeywordInputChange,
                onSearch = {
                    searchByInput()
                },
                resetSearch = resetSearch
            )

        }
    }
}

@Preview
@Composable
private fun SearchScreenPreview() {
    SearchScreen(
        navigateBack = {},
        keywordInput = "",
        recentKeywords = listOf("선행", "제비", "흥부", "선행자", "쓰레기"),
        isSearched = true,
        searchType = SearchType.CONTENT,
        tabType = SearchTab.ALL,
        displayedPosts = fakeLazyPagingPosts(),
        onKeywordInputChange = {},
        clearKeywords = {},
        onSearchTypeChange = {},
        onTabTypeChange = {},
        removeKeyword = {},
        searchByInput = {},
        searchByRecentKeyword = {},
        resetSearch = {},
        navigateToPost = {}
    )
}