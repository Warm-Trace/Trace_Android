package com.virtuous.designsystem.component

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.virtuous.designsystem.theme.PrimaryActive
import com.virtuous.designsystem.theme.TraceTheme
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
            .fillMaxWidth()
            .padding(horizontal = 28.dp)
            .padding(bottom = bottomOffset)
            .clip(RoundedCornerShape(8.dp))
            .background(PrimaryActive)
            .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp),
    ) {
        Text(
            text = message,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = TraceTheme.typography.bodySSB,
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

@Preview
@Composable
private fun TraceSnackBarPreview() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TraceSnackBar(
            snackBarData = object : SnackbarData {
                override val visuals: SnackbarVisuals = object : SnackbarVisuals {
                    override val actionLabel = null
                    override val duration = SnackbarDuration.Short
                    override val message = "로그인에 실패했습니다."
                    override val withDismissAction = false
                }

                override fun dismiss() {}
                override fun performAction() {}
            }
        )
    }
}


