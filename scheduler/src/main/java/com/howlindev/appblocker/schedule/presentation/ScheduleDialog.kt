package com.howlindev.appblocker.schedule.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.howlindev.appblocker.core.domain.model.Profile
import com.howlindev.appblocker.core.presentation.component.AppChip
import com.howlindev.appblocker.core.presentation.component.WebsiteChip
import com.howlindev.appblocker.schedule.R
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
    initialDays: Set<DayOfWeek> = emptySet(),
    title: String? = null,
    profiles: List<Profile>? = null,
    selectedProfileId: Long? = null,
    onProfileSelect: (Long) -> Unit = {},
    onCreateProfileClick: () -> Unit = {},
) {
    var fromTime by remember { mutableStateOf(initialFrom) }
    var untilTime by remember { mutableStateOf(initialUntil) }
    var selectedDays by remember { mutableStateOf(initialDays) }

    var showFromTimePicker by remember { mutableStateOf(false) }
    var showUntilTimePicker by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val noDaysSelectedError = stringResource(R.string.schedule_error_no_days_selected)
    val invalidTimeRangeError = stringResource(R.string.schedule_error_invalid_time_range)
    val noProfileSelectedError = stringResource(R.string.schedule_error_no_profile_selected)

    if (showFromTimePicker) {
        TimePickerDialog(
            initialTime = fromTime,
            onDismiss = { showFromTimePicker = false },
            onConfirm = { time ->
                fromTime = time
                showFromTimePicker = false
                errorMessage = null
            },
        )
    }

    if (showUntilTimePicker) {
        TimePickerDialog(
            initialTime = untilTime,
            onDismiss = { showUntilTimePicker = false },
            onConfirm = { time ->
                untilTime = time
                showUntilTimePicker = false
                errorMessage = null
            },
        )
    }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
    ) {
        Card {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = title ?: stringResource(R.string.schedule_dialog_edit_title),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                )

                if (profiles != null) {
                    ProfileSelector(
                        profiles = profiles,
                        selectedProfileId = selectedProfileId,
                        onProfileSelect = {
                            onProfileSelect(it)
                            errorMessage = null
                        },
                        onCreateProfileClick = onCreateProfileClick,
                    )
                }

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
                        errorMessage = null
                    },
                )

                AnimatedVisibility(
                    visible = errorMessage != null,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut(),
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.schedule_button_cancel))
                    }
                    TextButton(onClick = {
                        if (profiles != null && selectedProfileId == null) {
                            errorMessage = noProfileSelectedError
                            return@TextButton
                        }
                        if (selectedDays.isEmpty()) {
                            errorMessage = noDaysSelectedError
                            return@TextButton
                        }
                        if (untilTime <= fromTime && untilTime != LocalTime.MIDNIGHT) {
                            errorMessage = invalidTimeRangeError
                            return@TextButton
                        }
                        onConfirm(fromTime, untilTime, selectedDays)
                    }) {
                        Text(stringResource(R.string.schedule_button_confirm))
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileSelector(
    profiles: List<Profile>,
    selectedProfileId: Long?,
    onProfileSelect: (Long) -> Unit,
    onCreateProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedProfile = remember(profiles, selectedProfileId) {
        profiles.find { it.id == selectedProfileId }
    }

    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "ArrowRotation",
    )

    var width by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned {
                width = it.size.width
            },
    ) {
        Button(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
            ),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = selectedProfile?.name ?: stringResource(R.string.schedule_dialog_select_profile),
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.rotate(rotationAngle),
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(with(LocalDensity.current) { width.toDp() }),
        ) {
            profiles.forEach { profile ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = profile.name,
                            )
                            if (profile.appPackages.isNotEmpty()) {
                                AppChip(text = profile.appPackages.size.toString())
                            }
                            if (profile.blockedWebsites.isNotEmpty()) {
                                WebsiteChip(text = profile.blockedWebsites.size.toString())
                            }
                        }
                    },
                    onClick = {
                        onProfileSelect(profile.id)
                        expanded = false
                    },
                )
            }
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.schedule_dialog_new_profile),
                        fontWeight = FontWeight.ExtraBold,
                    )
                },
                onClick = {
                    onCreateProfileClick()
                    expanded = false
                },
            )
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
    onDayToggle: (DayOfWeek) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TimePickerRow(
            fromTime = fromTime,
            untilTime = untilTime,
            onFromClick = onFromClick,
            onUntilClick = onUntilClick,
        )

        DayOfWeekSelector(
            selectedDays = selectedDays,
            onDayToggle = onDayToggle,
        )
    }
}

@Composable
private fun TimePickerRow(
    fromTime: LocalTime,
    untilTime: LocalTime,
    onFromClick: () -> Unit,
    onUntilClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        OutlinedButton(
            onClick = onFromClick,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column {
                Text(
                    text = stringResource(R.string.schedule_label_from),
                    style = MaterialTheme.typography.labelSmall,
                )
                Text(
                    text = String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        fromTime.hour,
                        fromTime.minute,
                    ),
                    style = MaterialTheme.typography.headlineLarge,
                )
            }
        }
        OutlinedButton(
            onClick = onUntilClick,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column {
                Text(
                    text = stringResource(R.string.schedule_label_until),
                    style = MaterialTheme.typography.labelSmall,
                )
                Text(
                    text = String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        untilTime.hour,
                        untilTime.minute,
                    ),
                    style = MaterialTheme.typography.headlineLarge,
                )
            }
        }
    }
}

@Composable
private fun DayOfWeekSelector(
    selectedDays: Set<DayOfWeek>,
    onDayToggle: (DayOfWeek) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        DayOfWeek.entries.forEach { day ->
            val isSelected = selectedDays.contains(day)
            val firstLetter = day.getDisplayName(TextStyle.NARROW, Locale.getDefault())

            if (isSelected) {
                Button(
                    onClick = { onDayToggle(day) },
                    modifier = Modifier.size(32.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                    ),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(text = firstLetter)
                }
            } else {
                Button(
                    onClick = { onDayToggle(day) },
                    modifier = Modifier.size(32.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
                    shape = RoundedCornerShape(8.dp),
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
    onConfirm: (LocalTime) -> Unit,
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
    )

    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = stringResource(R.string.schedule_dialog_select_time_title),
                    style = MaterialTheme.typography.titleMedium,
                )

                TimePicker(state = timePickerState)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.schedule_button_cancel))
                    }
                    TextButton(onClick = {
                        onConfirm(LocalTime.of(timePickerState.hour, timePickerState.minute))
                    }) {
                        Text(stringResource(R.string.schedule_button_confirm))
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
                    onDayToggle = {},
                )
            }
        }
    }
}
