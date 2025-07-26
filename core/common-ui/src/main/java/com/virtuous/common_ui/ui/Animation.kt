package com.virtuous.common_ui.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun TraceBottomBarAnimation(
    visible: Boolean,
    modifier: Modifier = Modifier,
    contents: @Composable AnimatedVisibilityScope.() -> Unit,
) = AnimatedVisibility(
    visible = visible,
    content = contents,
    modifier = modifier,
)

fun defaultSlideFadeIn() = slideInHorizontally(
    animationSpec =
        tween(500)
) { it } + fadeIn(animationSpec = tween(500))

fun defaultSlideFadeOut() = slideOutHorizontally(
    animationSpec = tween(500)
) { it } + fadeOut(animationSpec = tween(500))

fun defaultSlideUpFadeIn() = slideInVertically(
    animationSpec = tween(500)
) { it } + fadeIn(animationSpec = tween(500))

fun defaultSlideDownFadeOut() = slideOutVertically(
    animationSpec = tween(500)
) { it } + fadeOut(animationSpec = tween(500))

