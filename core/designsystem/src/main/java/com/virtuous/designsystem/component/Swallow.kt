package com.virtuous.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.virtuous.common_ui.util.clickable
import com.virtuous.designsystem.R
import com.virtuous.designsystem.theme.TraceTheme
import com.virtuous.domain.model.user.SwallowLevel

@Composable
fun SwallowProfile(
    level: SwallowLevel,
    navigateToSwallow: () -> Unit
) {
    val swallowRes = getSwallowRes(level)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { navigateToSwallow() }
    ) {
        Image(
            painter = painterResource(swallowRes),
            contentDescription = level.label,
            modifier = Modifier.size(70.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(Modifier.height(5.dp))

        Text("현재 레벨: ${level.label}", style = TraceTheme.typography.bodySM)
    }
}

fun getSwallowRes(level: SwallowLevel): Int = when (level) {
    SwallowLevel.EGG -> R.drawable.swallow_egg
    SwallowLevel.BABY_BIRD -> R.drawable.baby_swallow
    SwallowLevel.YOUNG_BIRD -> R.drawable.young_swallow
    SwallowLevel.ADOLESCENT_BIRD -> R.drawable.adolescent_swallow
    SwallowLevel.ADULT_BIRD -> R.drawable.adult_swallow
    SwallowLevel.LEADER_BIRD -> R.drawable.leader_swallow
    SwallowLevel.LEGENDARY_SWALLOW -> R.drawable.question_mark_ic
}