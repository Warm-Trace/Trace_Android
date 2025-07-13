package com.virtuous.home.graph.post.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.virtuous.designsystem.R
import com.virtuous.designsystem.component.CheckCancelDialog
import com.virtuous.designsystem.component.DropdownMenuItem
import com.virtuous.designsystem.component.TraceDropDownMenu

@Composable
internal fun OwnPostDropdownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onRefresh: () -> Unit,
    onUpdate: () -> Unit,
    onDelete: () -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    if (showDeleteDialog) {
        CheckCancelDialog(
            onCheck = {
                showDeleteDialog = false
                onDelete()
            },
            onDismiss = { showDeleteDialog = false },
            dialogText = "정말 삭제하시겠습니까?",
        )
    }

    TraceDropDownMenu(
        expanded = expanded,
        onDismiss = onDismiss,
        items = listOf(
            DropdownMenuItem(
                imageVector = Icons.Outlined.Refresh,
                labelRes = R.string.refresh,
                action = onRefresh,
            ),
            DropdownMenuItem(
                iconRes = R.drawable.edit_ic,
                labelRes = R.string.edit,
                action = onUpdate,
            ),
            DropdownMenuItem(
                iconRes = R.drawable.delete_ic,
                labelRes = R.string.delete,
                action = { showDeleteDialog = true },
            ),
        ),
    )
}

@Composable
internal fun OtherPostDropdownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onRefresh: () -> Unit,
    onReport: (String) -> Unit,
) {
    var isReportDropdownMenuExpanded by remember { mutableStateOf(false) }
    ReportDropDownMenu(
        expanded = isReportDropdownMenuExpanded,
        onDismiss = { isReportDropdownMenuExpanded = false },
        onReport = onReport,
    )

    TraceDropDownMenu(
        expanded = expanded,
        onDismiss = onDismiss,
        items = listOf(
            DropdownMenuItem(
                imageVector = Icons.Outlined.Refresh,
                labelRes = R.string.refresh,
                action = onRefresh,
            ),
            DropdownMenuItem(
                iconRes = R.drawable.report_ic,
                labelRes = R.string.report,
                action = { isReportDropdownMenuExpanded = true },
            ),
        ),
    )
}