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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.virtuous.common_ui.util.clickable
import com.virtuous.designsystem.R
import com.virtuous.designsystem.theme.PrimaryDefault
import com.virtuous.designsystem.theme.TraceTheme

@Composable
fun ProfileImage(
    modifier: Modifier = Modifier,
    profileImageUrl: String?,
    size: Int,
    navigateToUserProfile: () -> Unit = {},
) {
    val profileImage = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(profileImageUrl ?: R.drawable.default_profile).build()
    )

    Image(
        painter = profileImage,
        contentDescription = "프로필 이미지",
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .clickable {
                navigateToUserProfile()
            },
        contentScale = ContentScale.Crop
    )
}

@Preview
@Composable
private fun ProfileImagePreview() {
    TraceTheme {
        ProfileImage(
            profileImageUrl = null,
            size = 40
        )
    }
}