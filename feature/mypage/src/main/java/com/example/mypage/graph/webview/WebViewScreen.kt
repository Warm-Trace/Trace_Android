package com.example.mypage.graph.webview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.common.ui.TraceWebView

@Composable
internal fun WebViewRoute(
    url: String,
    viewModel: WebViewViewModel = hiltViewModel()
) {
    WebViewScreen(
        url = url,
    )
}

@Composable
private fun WebViewScreen(
    url: String,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TraceWebView(
            url = url,
            modifier = Modifier
                .imePadding(),
        )
    }
}