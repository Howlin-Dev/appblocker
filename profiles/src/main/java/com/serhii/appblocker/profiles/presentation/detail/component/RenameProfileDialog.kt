package com.serhii.appblocker.profiles.presentation.detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenameProfileDialog(
    name: String,
    onChange: (String) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var name: String by remember { mutableStateOf(name) }

    BasicAlertDialog(
        modifier = modifier,
        onDismissRequest = onCancel,
    ) {
        Card {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Rename Profile",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    maxLines = 1,
                    label = {
                        Text(text = "Name")
                    },
                    placeholder = {
                        Text(text = "Name")
                    },
                )
                Row {
                    TextButton(
                        onClick = onCancel,
                    ) {
                        Text("Cancel")
                    }
                    TextButton(
                        onClick = { onChange(name) },
                    ) {
                        Text("Apply")
                    }
                }
            }
        }
    }
}
