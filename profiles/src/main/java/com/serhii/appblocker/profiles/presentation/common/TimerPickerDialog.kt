package com.serhii.appblocker.profiles.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.serhii.appblocker.core.util.millisToTimeString
import com.serhii.appblocker.profiles.util.timerUiTimeList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerPickerDialog(
    time: Long?,
    onConfirm: (Long?) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    timerValues: List<Long?> = timerUiTimeList,
) {
    var selectedTime by remember { mutableStateOf(time) }

    BasicAlertDialog(
        modifier = modifier,
        onDismissRequest = onCancel,
    ) {
        Card {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Change Timer",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                )
                timerValues.chunked(3).forEach { chunk ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        chunk.forEach {
                            TimeItem(
                                modifier = Modifier.weight(1f),
                                time = it,
                                isSelected = it == selectedTime,
                                onClick = { selectedTime = it }
                            )
                        }
                    }
                }
                Row {
                    TextButton(
                        onClick = onCancel
                    ) {
                        Text("Cancel")
                    }
                    TextButton(
                        onClick = { onConfirm(selectedTime) }
                    ) {
                        Text("Apply")
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeItem(
    time: Long?,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(size = 6.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            text = time.millisToTimeString(),
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
private fun TimerPickerDialogPreview() {
    Surface {
        TimerPickerDialog(
            time = 120_000,
            onConfirm = { },
            onCancel = { },
        )
//        TimeItem(
//            time = 60_000,
//            isSelected = true,
//        )
    }
}
