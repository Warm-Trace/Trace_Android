package com.virtuous.designsystem.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf


private val LocalTypography = staticCompositionLocalOf {
    TraceTypography()
}


@Composable
fun TraceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(content = content)
}

object TraceTheme {
    val typography: TraceTypography
        @Composable
        get() = LocalTypography.current
}