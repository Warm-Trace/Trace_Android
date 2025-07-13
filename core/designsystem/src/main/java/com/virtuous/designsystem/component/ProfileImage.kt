package com.virtuous.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.virtuous.designsystem.R
import com.virtuous.designsystem.theme.PrimaryDefault

@Composable
fun ProfileImage(
    profileImageUrl: String?,
    imageSize: Dp,
    paddingValue: Dp,
    strokeWidth : Float = 4f,
) {
    val profileImage = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(profileImageUrl ?: R.drawable.default_profile).crossfade(true).build()
    )

    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(imageSize + paddingValue)) {
            val canvasWidth = size.width
            val center = center
            val radius = canvasWidth / 2f

            drawCircle(
                color = PrimaryDefault,
                radius = radius,
                center = center,
                style = Stroke(strokeWidth)
            )
        }


        Image(
            painter = profileImage,
            contentDescription = "프로필 이미지",
            modifier = Modifier
                .size(imageSize)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }

}