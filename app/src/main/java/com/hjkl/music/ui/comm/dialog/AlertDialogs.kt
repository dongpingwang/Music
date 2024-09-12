package com.hjkl.music.ui.comm.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun EasyAlertDialog(
    dialogText: String,
    confirmBtnText: String,
    dismissBtnText: String,
    onConfirmation: () -> Unit,
    onDismissRequest: () -> Unit = {},
) {
    AlertDialog(
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(confirmBtnText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(dismissBtnText)
            }
        }
    )
}

@Preview
@Composable
private fun EasyAlertDialogPreview() {
    EasyAlertDialog(
        dialogText = "内容",
        confirmBtnText = "确认",
        dismissBtnText = "取消",
        onConfirmation = {})
}