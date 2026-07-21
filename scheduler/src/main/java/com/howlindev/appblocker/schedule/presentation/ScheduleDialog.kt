package com.howlindev.appblocker.schedule.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDialog(
    onDismiss: () -> Unit,
    onConfirm: (from: LocalTime, until: LocalTime, days: Set<DayOfWeek>) -> Unit,
    modifier: Modifier = Modifier,
    initialFrom: LocalTime = LocalTime.of(9, 0),
    initialUntil: LocalTime = LocalTime.of(17, 0),
    initialDays: Set<DayOfWeek> = emptySet()
) {
    var fromTime by remember { mutableStateOf(initialFrom) }
    var untilTime by remember { mutableStateOf(initialUntil) }
    var selectedDays by remember { mutableStateOf(initialDays) }

    var showFromTimePicker by remember { mutableStateOf(false) }
    var showUntilTimePicker by remember { mutableStateOf(false) }

    if (showFromTimePicker) {
        TimePickerDialog(
            initialTime = fromTime,
            onDismiss = { showFromTimePicker = false },
            onConfirm = { time ->
                fromTime = time
                showFromTimePicker = false
            }
        )
    }

    if (showUntilTimePicker) {
        TimePickerDialog(
            initialTime = untilTime,
            onDismiss = { showUntilTimePicker = false },
            onConfirm = { time ->
                untilTime = time
                showUntilTimePicker = false
            }
        )
    }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Card {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Edit Schedule",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                ScheduleDialogBody(
                    fromTime = fromTime,
                    untilTime = untilTime,
                    selectedDays = selectedDays,
                    onFromClick = { showFromTimePicker = true },
                    onUntilClick = { showUntilTimePicker = true },
                    onDayToggle = { day ->
                        selectedDays = if (selectedDays.contains(day)) {
                            selectedDays - day
                        } else {
                            selectedDays + day
                        }
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onConfirm(fromTime, untilTime, selectedDays) }) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleDialogBody(
    fromTime: LocalTime,
    untilTime: LocalTime,
    selectedDays: Set<DayOfWeek>,
    onFromClick: () -> Unit,
    onUntilClick: () -> Unit,
    onDayToggle: (DayOfWeek) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TimePickerRow(
            fromTime = fromTime,
            untilTime = untilTime,
            onFromClick = onFromClick,
            onUntilClick = onUntilClick
        )

        DayOfWeekSelector(
            selectedDays = selectedDays,
            onDayToggle = onDayToggle
        )
    }
}

@Composable
private fun TimePickerRow(
    fromTime: LocalTime,
    untilTime: LocalTime,
    onFromClick: () -> Unit,
    onUntilClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onFromClick,
            modifier = Modifier.weight(1f)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "From", style = MaterialTheme.typography.labelSmall)
                Text(text = String.format(Locale.getDefault(), "%02d:%02d", fromTime.hour, fromTime.minute), style = MaterialTheme.typography.bodyLarge)
            }
        }
        OutlinedButton(
            onClick = onUntilClick,
            modifier = Modifier.weight(1f)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Until", style = MaterialTheme.typography.labelSmall)
                Text(text = String.format(Locale.getDefault(), "%02d:%02d", untilTime.hour, untilTime.minute), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
private fun DayOfWeekSelector(
    selectedDays: Set<DayOfWeek>,
    onDayToggle: (DayOfWeek) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        DayOfWeek.values().forEach { day ->
            val isSelected = selectedDays.contains(day)
            val firstLetter = day.getDisplayName(TextStyle.NARROW, Locale.getDefault())
            
            if (isSelected) {
                OutlinedButton(
                    onClick = { onDayToggle(day) },
                    modifier = Modifier.width(44.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                ) {
                    Text(text = firstLetter)
                }
            } else {
                TextButton(
                    onClick = { onDayToggle(day) },
                    modifier = Modifier.width(44.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                ) {
                    Text(text = firstLetter)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    initialTime: LocalTime,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute
    )

    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Select Time",
                    style = MaterialTheme.typography.titleMedium
                )
                
                TimePicker(state = timePickerState)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    TextButton(onClick = {
                        onConfirm(LocalTime.of(timePickerState.hour, timePickerState.minute))
                    }) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ScheduleDialogBodyPreview() {
    MaterialTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                ScheduleDialogBody(
                    fromTime = LocalTime.of(9, 0),
                    untilTime = LocalTime.of(17, 30),
                    selectedDays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
                    onFromClick = {},
                    onUntilClick = {},
                    onDayToggle = {}
                )
            }
        }
    }
}
