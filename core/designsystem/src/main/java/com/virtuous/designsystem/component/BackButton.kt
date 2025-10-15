package com.virtuous.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.virtuous.designsystem.R

@Composable
fun BackButton(
    navigateBack: () -> Unit,
    @DrawableRes icon: Int = R.drawable.arrow_left_ic,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = { navigateBack() },
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = "뒤로 가기",
            modifier = Modifier.size(24.dp),
        )
    }
}
