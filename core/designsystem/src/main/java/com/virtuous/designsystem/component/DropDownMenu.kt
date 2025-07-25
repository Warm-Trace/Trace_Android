package com.virtuous.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.virtuous.common_ui.util.clickable
import com.virtuous.designsystem.R
import com.virtuous.designsystem.theme.TraceTheme
import com.virtuous.designsystem.theme.White

data class DropdownMenuItem(
    val iconRes: Int? = null,
    val imageVector: ImageVector? = null,
    val labelRes: Int,
    val action: () -> Unit
)

@Composable
fun TraceDropDownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    title: String? = null,
    width: Int = 160,
    items: List<DropdownMenuItem>
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .shadow(1.dp, RoundedCornerShape(16.dp))
            .background(White)
            .padding(start = 16.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        if(title != null) {
            Text(
                text = title,
                style = TraceTheme.typography.bodySSB,
            )

            Spacer(Modifier.height(20.dp))
        }

        items.forEachIndexed { index, item ->
            if(index != 0) Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .clickable {
                        onDismiss()
                        item.action()
                    }
                    .widthIn(min = width.dp),
            ) {
                when {
                    item.imageVector != null -> {
                        Icon(
                            imageVector = item.imageVector,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    item.iconRes != null -> {
                        Image(
                            painter = painterResource(item.iconRes),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))
                    }
                }

                Text(
                    text = stringResource(item.labelRes),
                    style = TraceTheme.typography.bodyMR
                )
            }

            if(index == items.lastIndex) Spacer(Modifier.height(12.dp))
        }


    }
}

@Preview
@Composable
private fun TraceDropDownMenuPreview() {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        TraceDropDownMenu(
            expanded = true,
            onDismiss = {},
            items = listOf(
                DropdownMenuItem(
                    imageVector = Icons.Outlined.Refresh,
                    labelRes = R.string.refresh,
                    action = {}
                ),
                DropdownMenuItem(
                    imageVector = Icons.Outlined.Refresh,
                    labelRes = R.string.refresh,
                    action = {}
                ),
                DropdownMenuItem(
                    imageVector = Icons.Outlined.Refresh,
                    labelRes = R.string.refresh,
                    action = {}
                )
            )
        )
    }
}