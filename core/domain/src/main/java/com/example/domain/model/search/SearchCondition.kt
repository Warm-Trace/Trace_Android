package com.example.domain.model.search


data class SearchCondition(
    val keyword: String,
    val tabType: SearchTab,
    val searchType: SearchType,
    val isSearched: Boolean
)