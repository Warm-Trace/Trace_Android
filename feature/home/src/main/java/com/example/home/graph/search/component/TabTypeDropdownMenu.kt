package com.example.home.graph.search.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.common.util.clickable
import com.example.designsystem.theme.Background
import com.example.designsystem.theme.TraceTheme
import com.example.domain.model.search.SearchTab

@Composable
internal fun TabTypeDropdownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    selectedTabType: SearchTab,
    onTabTypeChange: (SearchTab) -> Unit
) {
    val entries = SearchTab.entries

    if (expanded) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismiss,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .background(Background),
        ) {
            entries.forEach { tabType ->
                Row(
                    modifier = Modifier
                        .clickable(isRipple = true) {
                            onTabTypeChange(tabType)
                            onDismiss()
                        }
                        .padding(top = 15.dp, bottom = 15.dp, start = 15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(tabType.label, style = TraceTheme.typography.bodySSB)

                    Spacer(Modifier.width(65.dp))

                    if (selectedTabType == tabType) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "선택된 게시글 타입",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }


        }
    }
}

@Preview
@Composable
private fun TabTypeDropdownMenuPreview() {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        TabTypeDropdownMenu(
            expanded = true,
            onTabTypeChange = {},
            onDismiss = {},
            selectedTabType = SearchTab.ALL
        )
    }
}