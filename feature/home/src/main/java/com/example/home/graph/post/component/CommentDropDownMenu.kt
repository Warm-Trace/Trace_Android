package com.example.home.graph.post.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.designsystem.R
import com.example.designsystem.component.CheckCancelDialog
import com.example.designsystem.component.DropdownMenuItem
import com.example.designsystem.component.TraceDropDownMenu

@Composable
internal fun OwnCommentDropdownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onReply: () -> Unit,
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
                iconRes = R.drawable.reply,
                labelRes = R.string.reply,
                action = onReply,
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
internal fun OtherCommentDropdownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onReply: () -> Unit,
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
                iconRes = R.drawable.reply,
                labelRes = R.string.reply,
                action = onReply,
            ),
            DropdownMenuItem(
                iconRes = R.drawable.report_ic,
                labelRes = R.string.report,
                action = { isReportDropdownMenuExpanded = true },
            ),
        ),
    )
}

@Composable
internal fun OwnChildCommentDropdownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        CheckCancelDialog(
            onCheck = {
                onDelete()
                showDeleteDialog = false
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
                iconRes = R.drawable.delete_ic,
                labelRes = R.string.delete,
                action = { showDeleteDialog = true },
            ),
        ),
    )
}

@Composable
internal fun OtherChildCommentDropdownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
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
                iconRes = R.drawable.report_ic,
                labelRes = R.string.report,
                action = { isReportDropdownMenuExpanded = true },
            ),
        ),
    )
}