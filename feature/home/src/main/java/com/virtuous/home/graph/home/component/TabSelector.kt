package com.virtuous.home.graph.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.virtuous.common.util.clickable
import com.virtuous.designsystem.theme.Background
import com.virtuous.designsystem.theme.PrimaryActive
import com.virtuous.designsystem.theme.TraceTheme
import com.virtuous.designsystem.theme.WarmGray
import com.virtuous.designsystem.theme.White
import com.virtuous.domain.model.post.HomeTab

@Composable
internal fun TabSelector(
    type: HomeTab,
    selectedType: HomeTab,
    onTabSelected: (HomeTab) -> Unit
) {
    Box(
        modifier = Modifier
            .width(45.dp)
            .height(35.dp)
            .border(
                width = 1.dp,
                color = if (type == selectedType) Color.Transparent else WarmGray,
                shape = RoundedCornerShape(10.dp)
            )
            .clip(RoundedCornerShape(10.dp))
            .background(if (type == selectedType) PrimaryActive else Background)
            .clickable {
                onTabSelected(type)
            },
    ) {
        Text(
            type.label, style = TraceTheme.typography.homeTab,
            color = if (type == selectedType) White else WarmGray,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}