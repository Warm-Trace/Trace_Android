package com.example.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.common.util.clickable
import com.example.designsystem.R
import com.example.designsystem.theme.TraceTheme
import com.example.designsystem.theme.White

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
    items: List<DropdownMenuItem>
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .background(White)
    ) {
        items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .clickable {
                        onDismiss()
                        item.action()
                    }
                    .padding(
                        start = 12.dp,
                        top = if (index == 0) 10.dp else 20.dp,
                        bottom = if(index == items.lastIndex) 10.dp else 0.dp
                    )
                    .widthIn(min = 170.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when {
                    item.imageVector != null -> {
                        Icon(
                            imageVector = item.imageVector,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    item.iconRes != null -> {
                        Image(
                            painter = painterResource(item.iconRes),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = stringResource(item.labelRes),
                    style = TraceTheme.typography.bodyMR
                )
            }
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