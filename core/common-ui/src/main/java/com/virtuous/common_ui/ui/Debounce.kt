package com.virtuous.common_ui.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun rememberDebouncedOnClick(
    timeoutMillis: Long = 500L, // 기본 타임아웃
    onClick: () -> Unit
): () -> Unit {
    var lastClickTime by remember { mutableStateOf(0L) }

    return {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > timeoutMillis) {
            lastClickTime = currentTime
            onClick()
        }
    }
}
