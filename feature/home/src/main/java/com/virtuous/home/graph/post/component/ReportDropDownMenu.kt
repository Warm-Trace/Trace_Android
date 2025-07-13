package com.virtuous.home.graph.post.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.virtuous.designsystem.R
import com.virtuous.designsystem.component.CheckCancelDialog
import com.virtuous.designsystem.component.DropdownMenuItem
import com.virtuous.designsystem.component.TraceDropDownMenu

@Composable
internal fun ReportDropDownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onReport: (String) -> Unit,
) {
    var showReportDialog by remember { mutableStateOf(false) }
    var selectedReason by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    val reportReasons = remember {
        mapOf(
            R.string.report_reason_profanity_slander to R.string.report_reason_profanity_slander_description,
            R.string.report_reason_illegal_content to R.string.report_reason_illegal_content_description,
            R.string.report_reason_obscene_content to R.string.report_reason_obscene_content_description,
            R.string.report_reason_fraud_misinformation to R.string.report_reason_fraud_misinformation_description,
            R.string.report_reason_commercial_spam to R.string.report_reason_commercial_spam_description,
            R.string.report_reason_off_topic to R.string.report_reason_off_topic_description,
            R.string.report_reason_unpleasant_user to R.string.report_reason_unpleasant_user_description,
        )
    }

    val currentReason = selectedReason
    if (showReportDialog && currentReason != null) {
        val reportReasonText = stringResource(id = currentReason.first)
        CheckCancelDialog(
            title = reportReasonText,
            onDismiss = {
                showReportDialog = false
                selectedReason = null
            },
            onCheck = {
                onReport(reportReasonText)
                showReportDialog = false
                selectedReason = null
            },
            dialogText = "${stringResource(id = currentReason.second)}\n\n${stringResource(id = R.string.default_report_description)}",
        )
    }

    val items = reportReasons.map { (reasonResId, _) ->
        DropdownMenuItem(
            labelRes = reasonResId,
            action = {
                selectedReason = reasonResId to reportReasons.getValue(reasonResId)
                showReportDialog = true
            },
        )
    }

    TraceDropDownMenu(
        title = "신고 사유 선택",
        width = 200,
        expanded = expanded,
        onDismiss = onDismiss,
        items = items,
    )
}

@Preview(showBackground = true)
@Composable
private fun ReportDropDownMenuPreview() {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        ReportDropDownMenu(
            expanded = true,
            onDismiss = {},
            onReport = {},
        )
    }
}