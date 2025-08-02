package com.virtuous.designsystem.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BackButton(
    navigateBack: () -> Unit
) {
    IconButton(
        onClick = { navigateBack() },
        modifier = Modifier
            .padding(start = 10.dp, top = 2.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
            contentDescription = "뒤로가기",
            modifier = Modifier.size(36.dp),
        )
    }
}
