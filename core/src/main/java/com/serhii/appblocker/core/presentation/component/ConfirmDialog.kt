package com.serhii.appblocker.core.presentation.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ConfirmDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    title: String,
    text: String,
    confirmButtonText: String,
    cancelButtonText: String,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        modifier = modifier,
        title = {
            Text(text = title)
        },
        text = {
            Text(text = text)
        },
        onDismissRequest = {
            onCancel()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                },
            ) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onCancel()
                },
            ) {
                Text(cancelButtonText)
            }
        },
    )
}
