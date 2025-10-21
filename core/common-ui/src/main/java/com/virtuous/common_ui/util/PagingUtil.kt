package com.virtuous.common_ui.util

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.paging.compose.LazyPagingItems

@Composable
fun <T : Any> LazyPagingItems<T>.rememberLazyListState(): LazyListState {
    return when (itemCount) {
        0 -> {
            remember(this) { LazyListState(0, 0) }
        }
        else -> {
            androidx.compose.foundation.lazy.rememberLazyListState()
        }
    }
}