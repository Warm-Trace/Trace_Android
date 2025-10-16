package com.virtuous.home.graph.search

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.virtuous.common_ui.compositionlocal.LocalSnackbarHostState
import com.virtuous.designsystem.component.BackButton
import com.virtuous.designsystem.theme.Background
import com.virtuous.domain.model.post.PostFeed
import com.virtuous.domain.model.search.SearchTab
import com.virtuous.domain.model.search.SearchType
import com.virtuous.home.graph.home.fakeLazyPagingPosts
import com.virtuous.home.graph.search.SearchViewModel.SearchEvent
import com.virtuous.home.graph.search.component.SearchInitialView
import com.virtuous.home.graph.search.component.SearchResultView
import com.virtuous.home.graph.search.component.TraceSearchField
import kotlinx.coroutines.launch

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

    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(isSearched) {
        viewModel.loadRecentKeywords()
    }

    LaunchedEffect(Unit) {
        viewModel.eventChannel.collect { event ->
            when (event) {
                is SearchEvent.NavigateToPost -> navigateToPost(event.postFeed)
                is SearchEvent.NavigateBack -> navigateBack()
                is SearchEvent.ShowSnackbar -> {
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(event.message)
                    }
                }
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
        onBackButtonClick = navigateBack,
        onPostFeedClick = navigateToPost
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
    onBackButtonClick: () -> Unit,
    onPostFeedClick: (PostFeed) -> Unit,
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
                .padding(start = 20.dp, end = 20.dp, top = 55.dp)
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
                    navigateToPost = onPostFeedClick,
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(end = 20.dp)
                .padding(top = 10.dp, bottom = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton(onBackButtonClick)

            Spacer(Modifier.width(5.dp))

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

@Preview(name = "검색 전")
@Composable
private fun SearchScreenBeforePreview() {
    SearchScreen(
        onBackButtonClick = {},
        keywordInput = "",
        recentKeywords = listOf("선행", "제비", "흥부", "선행자", "쓰레기"),
        isSearched = false,
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
        onPostFeedClick = {}
    )
}

@Preview(name = "검색 후")
@Composable
private fun SearchScreenAfterPreview() {
    SearchScreen(
        onBackButtonClick = {},
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
        onPostFeedClick = {}
    )
}