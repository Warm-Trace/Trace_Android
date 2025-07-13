package com.virtuous.designsystem.component

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.virtuous.designsystem.theme.PrimaryDefault
import kotlinx.coroutines.delay

@Composable
fun TraceSnackBar(
    snackBarData: SnackbarData
) {
    val message = snackBarData.visuals.message

    val ime = WindowInsets.ime
    val density = LocalDensity.current
    val imeBottomPx = ime.getBottom(density)
    val bottomOffset = with(density) { imeBottomPx.toDp() + 36.dp }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(bottom = bottomOffset, start = 20.dp, end = 20.dp)
            .wrapContentSize()
            .clip(RoundedCornerShape(12.dp))
            .background(PrimaryDefault)
            .padding(horizontal = 30.dp, vertical = 8.dp),
    ) {
        Text(
            text = message,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
            color = Color.White,
        )
    }
}

@Composable
fun TraceSnackBarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    snackbar: @Composable (SnackbarData) -> Unit = { Snackbar(it) }
) {
    val currentSnackbarData = hostState.currentSnackbarData

    LaunchedEffect(currentSnackbarData) {
        if (currentSnackbarData != null) {
            delay(1500L)
            currentSnackbarData.dismiss()
        }
    }

    Crossfade(
        targetState = hostState.currentSnackbarData,
        modifier = modifier,
        label = "",
        content = { current -> if (current != null) snackbar(current) },
    )
}

