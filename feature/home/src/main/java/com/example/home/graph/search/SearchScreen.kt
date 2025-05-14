package com.example.home.graph.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.common.util.clickable
import com.example.designsystem.R
import com.example.designsystem.theme.Background
import com.example.designsystem.theme.PrimaryDefault
import com.example.domain.model.post.PostFeed
import com.example.domain.model.post.SearchType
import com.example.home.graph.search.SearchViewModel.SearchEvent
import com.example.home.graph.search.component.SearchInitialView
import com.example.home.graph.search.component.SearchResultView
import com.example.home.graph.search.component.TraceSearchField

@Composable
internal fun SearchRoute(
    navigateBack: () -> Unit,
    navigateToPost: (Int) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val keywordInput by viewModel.keywordInput.collectAsStateWithLifecycle()
    val recentKeywords by viewModel.recentKeywords.collectAsStateWithLifecycle()
    val isSearched by viewModel.isSearched.collectAsStateWithLifecycle()
    val searchType by viewModel.searchType.collectAsStateWithLifecycle()
    val titleMatchedPosts by viewModel.titleMatchedPosts.collectAsStateWithLifecycle()
    val contentMatchedPosts by viewModel.contentMatchedPosts.collectAsStateWithLifecycle()

    LaunchedEffect(isSearched) {
        viewModel.loadRecentKeywords()
    }

    LaunchedEffect(Unit) {
        viewModel.eventChannel.collect { event ->
            when (event) {
                is SearchEvent.NavigateToPost -> navigateToPost(event.postId)
                is SearchEvent.NavigateBack -> navigateBack()
            }
        }
    }

    SearchScreen(
        keywordInput = keywordInput,
        recentKeywords = recentKeywords,
        isSearched = isSearched,
        searchType = searchType,
        displayedPosts = when (searchType) {
            SearchType.CONTENT -> contentMatchedPosts
            SearchType.TITLE -> titleMatchedPosts
        },
        onKeywordInputChange = viewModel::setKeywordInput,
        onSearchTypeChange = viewModel::setSearchType,
        removeKeyword = viewModel::removeKeyword,
        clearKeywords = viewModel::clearKeywords,
        searchByInput = viewModel::searchByInput,
        searchByRecentKeyword = viewModel::searchByRecentKeyword,
        navigateBack = { viewModel.onEvent(SearchEvent.NavigateBack) },
        navigateToPost = { postId -> viewModel.onEvent(SearchEvent.NavigateToPost(postId)) }
    )
}

@Composable
private fun SearchScreen(
    keywordInput: String,
    recentKeywords: List<String>,
    isSearched: Boolean,
    searchType: SearchType,
    displayedPosts: List<PostFeed>,
    onKeywordInputChange: (String) -> Unit,
    onSearchTypeChange: (SearchType) -> Unit,
    searchByInput: () -> Unit,
    searchByRecentKeyword: (String) -> Unit,
    removeKeyword: (String) -> Unit,
    clearKeywords: () -> Unit,
    navigateBack: () -> Unit,
    navigateToPost: (Int) -> Unit,
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp, top = 50.dp)
        ) {
            item {
                Spacer(Modifier.height(20.dp))

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
                        onSearchTypeChange = onSearchTypeChange,
                        displayedPosts = displayedPosts,
                        navigateToPost = navigateToPost,
                    )
                }

                Spacer(Modifier.height(100.dp))
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(
                    PrimaryDefault
                )
                .padding(horizontal = 20.dp)
                .height(50.dp),
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
                    keyboardController?.hide()
                    searchByInput()
                },
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
        displayedPosts = fakePostFeeds,
        onKeywordInputChange = {}, clearKeywords = {},
        onSearchTypeChange = {},
        removeKeyword = {},
        searchByInput = {},
        searchByRecentKeyword = {},
        navigateToPost = {}
    )
}