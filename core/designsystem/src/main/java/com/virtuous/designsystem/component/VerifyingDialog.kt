package com.virtuous.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.virtuous.designsystem.R
import com.virtuous.designsystem.theme.Sky
import com.virtuous.designsystem.theme.TraceTheme
import com.virtuous.designsystem.theme.White
import kotlinx.coroutines.delay

@Composable
fun VerifyingDialog(
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.linearGradient(
                        colorStops = arrayOf(
                            0.0f to Sky,
                            0.3f to Sky,
                            0.5f to White,
                            1.0f to White,
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(0f, Float.POSITIVE_INFINITY)
                    )
                )
                .padding(start = 40.dp ,end = 40.dp, top = 100.dp, bottom = 60.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BirdFlyingAnimation()

                Spacer(modifier = Modifier.height(55.dp))

                Text(
                    "당신의 따뜻한 흔적을 인증 중이에요.",
                    style = TraceTheme.typography.bodyXMR
                )

                Spacer(Modifier.height(5.dp))

                Text(
                    "조금만 기다려주세요!",
                    style = TraceTheme.typography.bodyXMR
                )
            }
        }
    }
}

@Composable
fun BirdFlyingAnimation() {
    val frameImages = listOf(
        R.drawable.swallow_frame_1,
        R.drawable.swallow_frame_2,
        R.drawable.swallow_frame_3,
        R.drawable.swallow_frame_4,
        R.drawable.swallow_frame_5,
        R.drawable.swallow_frame_6,
    )

    var frameIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(120L)
            frameIndex = (frameIndex + 1) % frameImages.size
        }
    }

    Image(
        painter = painterResource(id = frameImages[frameIndex]),
        contentDescription = "날고있는 제비",
        modifier = Modifier
            .width(135.dp)
            .height(66.dp)
    )
}

@Preview
@Composable
fun VerifyingDialogPreview() {
    Column(modifier = Modifier.fillMaxSize()) {
        VerifyingDialog()
    }
}
