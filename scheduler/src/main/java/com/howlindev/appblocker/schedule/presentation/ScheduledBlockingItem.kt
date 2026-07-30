package com.howlindev.appblocker.schedule.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.howlindev.appblocker.schedule.R
import com.howlindev.appblocker.schedule.domain.model.ScheduleEvent
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ScheduledBlockingItem(
    event: ScheduleEvent,
    onEditClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val contentAlpha = if (isEnabled) 1f else 0.38f
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = String.format(
                    Locale.getDefault(),
                    "%02d:%02d - %02d:%02d",
                    event.startTime.hour,
                    event.startTime.minute,
                    event.endTime.hour,
                    event.endTime.minute,
                ),
                style = MaterialTheme.typography.titleMedium,
                fontSize = MaterialTheme.typography.titleMedium.fontSize.value.plus(2).sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha),
            )
            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DayOfWeek.entries.forEach { day ->
                    val isSelected = event.daysOfWeek.contains(day)
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.secondary.copy(alpha = contentAlpha)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = contentAlpha)
                                },
                                shape = RoundedCornerShape(4.dp),
                            )
                            .size(24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = day.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
                            textAlign = TextAlign.Center,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.onSecondary.copy(alpha = contentAlpha)
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha)
                            },
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
        }
        IconButton(
            onClick = { onEditClick() },
            enabled = isEnabled,
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(R.string.schedule_content_description_edit),
            )
        }
        IconButton(
            onClick = { onRemoveClick() },
            enabled = isEnabled,
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.schedule_content_description_remove),
            )
        }
    }
}

@Preview
@Composable
private fun ScheduledBlockingItemPreview() {
    Surface {
        ScheduledBlockingItem(
            event = ScheduleEvent(
                startTime = java.time.LocalTime.of(11, 15),
                endTime = java.time.LocalTime.of(13, 0),
                daysOfWeek = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
            ),
            onEditClick = {},
            onRemoveClick = {},
        )
    }
}
