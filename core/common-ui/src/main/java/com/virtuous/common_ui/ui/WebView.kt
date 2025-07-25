package com.virtuous.common_ui.ui

import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri

@Composable
fun TraceWebView(
    url: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var webView by remember { mutableStateOf<WebView?>(null) }

    AndroidView(
        factory = {
            webView = WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val url = request?.url?.toString() ?: return false
                        val context = view?.context ?: return false
                        return handleUrl(context, url)
                    }

                    private fun handleUrl(context: Context, url: String): Boolean {
                        if (url.startsWith("intent://")) {
                            try {
                                val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                                intent.getPackage()?.let {
                                    // 해당 인텐트를 실행할 앱이 설치되어 있으면 실행, 없으면 마켓으로 이동
                                    if (context.packageManager.getLaunchIntentForPackage(it) != null) {
                                        context.startActivity(intent)
                                    } else {
                                        val marketIntent = Intent(Intent.ACTION_VIEW)
                                        marketIntent.data = "market://details?id=$it".toUri()
                                        context.startActivity(marketIntent)
                                    }
                                }
                                return true
                            } catch (e: Exception) {
                                e.printStackTrace()
                                return false
                            }
                        }

                        return true
                    }
                }
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
            }
            webView!!
        },
        update = { it.loadUrl(url) },
        onRelease = { webView?.destroy() },
        modifier = modifier,
    )
}