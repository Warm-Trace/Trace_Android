package com.virtuous.home.graph.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.virtuous.common_ui.event.EventHelper
import com.virtuous.common_ui.event.TraceEvent
import com.virtuous.domain.model.post.PostFeed
import com.virtuous.domain.model.search.SearchCondition
import com.virtuous.domain.model.search.SearchTab
import com.virtuous.domain.model.search.SearchType
import com.virtuous.domain.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    val eventHelper: com.virtuous.common_ui.event.EventHelper
) : ViewModel() {
    private val _eventChannel = Channel<SearchEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    internal fun onEvent(event: SearchEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    private val _recentKeywords = MutableStateFlow<List<String>>(emptyList())
    val recentKeywords = _recentKeywords.asStateFlow()

    private val _keywordInput = MutableStateFlow("")
    val keywordInput = _keywordInput.asStateFlow()

    private val _isSearched = MutableStateFlow(false)
    val isSearched = _isSearched.asStateFlow()

    private val _searchType = MutableStateFlow(SearchType.ALL)
    val searchType = _searchType.asStateFlow()

    private val _tabType = MutableStateFlow(SearchTab.ALL)
    val tabType = _tabType.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val postFeeds = combine(
        _keywordInput,
        _tabType,
        _searchType,
        _isSearched
    ) { keyword, tab, searchType, isSearched ->
        SearchCondition(keyword, tab, searchType, isSearched)
    }.filter { it.isSearched && it.keyword.isNotBlank() }
        .map { Triple(it.keyword, it.tabType, it.searchType) }
        .flatMapLatest { (keyword, tab, searchType) ->
            searchRepository.searchPosts(
                keyword = keyword,
                tabType = tab,
                searchType = searchType
            )
        }
        .cachedIn(viewModelScope)

    init {
        loadRecentKeywords()
    }

    fun resetSearch() {
        _isSearched.value = false
    }

    fun setSearchType(searchType: SearchType) {
        _searchType.value = searchType
    }

    fun setTabType(tabType: SearchTab) {
        _tabType.value = tabType
    }

    fun setKeywordInput(keywordInput: String) {
        _keywordInput.value = keywordInput
    }

    fun searchByInput() = viewModelScope.launch {
        if (_keywordInput.value.isEmpty()) {
            eventHelper.sendEvent(com.virtuous.common_ui.event.TraceEvent.ShowSnackBar("검색할 키워드를 입력해주세요."))
            return@launch
        }

        if (_keywordInput.value.length < MIN_SEARCH_LENGTH) {
            eventHelper.sendEvent(com.virtuous.common_ui.event.TraceEvent.ShowSnackBar("검색어는 두 글자 이상 입력해 주세요."))
            return@launch
        }

        _isSearched.value = true
        addKeyword(_keywordInput.value)
    }

    fun searchByRecentKeyword(keyword: String) {
        _keywordInput.value = keyword
        _isSearched.value = true
        addKeyword(keyword)
    }

    fun loadRecentKeywords() = viewModelScope.launch {
        _recentKeywords.value = searchRepository.getRecentKeywords().getOrNull() ?: emptyList()
    }

    private fun addKeyword(keyword: String) = viewModelScope.launch {
        searchRepository.addKeyword(keyword)
    }

    fun removeKeyword(keyword: String) = viewModelScope.launch {
        searchRepository.removeKeyword(keyword)
        loadRecentKeywords()
    }

    fun clearKeywords() = viewModelScope.launch {
        searchRepository.clearKeywords()
        loadRecentKeywords()
    }

    sealed class SearchEvent {
        data object NavigateBack : SearchEvent()
        data class NavigateToPost(val postFeed: PostFeed) : SearchEvent()
    }

    companion object {
        private const val MIN_SEARCH_LENGTH = 2
    }
}
