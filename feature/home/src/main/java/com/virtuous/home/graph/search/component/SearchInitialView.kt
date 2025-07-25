package com.virtuous.home.graph.search.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.virtuous.common_ui.util.clickable
import com.virtuous.designsystem.theme.Background
import com.virtuous.designsystem.theme.Black
import com.virtuous.designsystem.theme.Gray
import com.virtuous.designsystem.theme.LightGray
import com.virtuous.designsystem.theme.PrimaryDefault
import com.virtuous.designsystem.theme.TraceTheme

@Composable
internal fun SearchInitialView(
    recentKeywords: List<String>,
    onSearch: (String) -> Unit,
    removeKeyword: (String) -> Unit,
    clearKeywords: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        item {
            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("최근 검색어", style = TraceTheme.typography.bodySSB)

                Spacer(Modifier.weight(1f))

                if (recentKeywords.isNotEmpty()) {
                    TextButton(onClick = clearKeywords) {
                        Text(
                            "전체 삭제",
                            style = TraceTheme.typography.bodySM,
                            color = LightGray
                        )
                    }

                }
            }

            Spacer(Modifier.height(10.dp))

            if (recentKeywords.isEmpty()) {
                Spacer(Modifier.height(50.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "최근 검색어 내역이 없습니다.",
                        style = TraceTheme.typography.bodyMM,
                        color = Gray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            if (recentKeywords.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    recentKeywords.forEach { keyword ->
                        RecentKeyword(
                            value = keyword,
                            onSearch = onSearch,
                            removeKeyword = removeKeyword
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun RecentKeyword(
    value: String,
    onSearch: (String) -> Unit,
    removeKeyword: (String) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Card(
        modifier = Modifier
            .background(Background)
            .clickable(isRipple = true) {
                onSearch(value)
                keyboardController?.hide()
                focusManager.clearFocus()
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardColors(
            containerColor = Background,
            contentColor = Black,
            disabledContainerColor = Background,
            disabledContentColor = Black
        ),
        border = BorderStroke(1.dp, color = PrimaryDefault)
    ) {
        Box() {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 10.dp)
            ) {
                Text(value, style = TraceTheme.typography.bodyML)

                Spacer(Modifier.width(28.dp))
            }

            Box(
                modifier = Modifier
                    .size(24.dp) // 리플 크기 조정
                    .align(Alignment.CenterEnd)
                    .clip(CircleShape)
                    .clickable() {
                        removeKeyword(value)
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "최근 검색어 삭제",
                    modifier = Modifier
                        .size(18.dp)
                        .align(Alignment.Center)
                )
            }

        }

    }
}