package com.example.designsystem.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.designsystem.R
import com.example.designsystem.theme.DarkGray
import com.example.designsystem.theme.PrimaryDefault
import com.example.designsystem.theme.TraceTheme
import com.example.designsystem.theme.White

@Composable
fun CheckCancelDialog(
    onCheck: () -> Unit,
    onDismiss: () -> Unit,
    checkText: String? = null,
    dialogText: String,
    title: String? = null,
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            if (title != null) {
                Text(text = title, style = TraceTheme.typography.bodyMSB)
            }
        },
        text = { Text(dialogText, style = TraceTheme.typography.bodySM) },
        shape = RoundedCornerShape(8.dp),
        containerColor = White,
        confirmButton = {
            TextButton(onClick = {
                onDismiss()
                onCheck()
            }) {
                Text(
                    if (checkText == null) stringResource(R.string.dialog_confirm) else checkText,
                    color = PrimaryDefault,
                    style = TraceTheme.typography.bodySM,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(
                    stringResource(R.string.dialog_cancel),
                    color = DarkGray,
                    style = TraceTheme.typography.bodySM,
                )
            }
        },
    )
}