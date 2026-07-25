package com.howlindev.appblocker.schedule.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.howlindev.appblocker.schedule.domain.model.ScheduleEvent
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar

@Composable
fun ScheduleTimeline(
    events: List<ScheduleEvent>,
    dayOfWeek: DayOfWeek,
    onHourClick: (Int) -> Unit,
    onEventClick: (ScheduleEvent) -> Unit,
    modifier: Modifier = Modifier,
    hourHeight: Dp = 80.dp,
) {
    val scrollState = rememberScrollState()

    var currentDayOfWeek by remember { mutableStateOf(LocalDate.now().dayOfWeek) }
    var currentHour by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) }
    var currentMinute by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.MINUTE)) }

    LaunchedEffect(Unit) {
        while (true) {
            val calendar = Calendar.getInstance()
            currentDayOfWeek = LocalDate.now().dayOfWeek
            currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            currentMinute = calendar.get(Calendar.MINUTE)
            delay(timeMillis = 60000) // Update every minute
        }
    }

    val isToday = dayOfWeek == currentDayOfWeek

    val mergedEvents = remember(events) { mergeEventsByProfile(events) }
    val eventLayouts = remember(mergedEvents) { computeEventLayouts(mergedEvents) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(vertical = 16.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Time Labels Column
            Column(modifier = Modifier.width(64.dp)) {
                (0..23).forEach { hour ->
                    TimeLabel(hour = hour, height = hourHeight)
                }
            }

            BoxWithConstraints(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .weight(1f)
                    .height(hourHeight * 24),
            ) {
                val contentWidth = maxWidth

                Column {
                    (0..23).forEach { hour ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(hourHeight)
                                .clickable { onHourClick(hour) },
                        ) {
                            HorizontalDivider(
                                modifier = Modifier.align(Alignment.TopStart),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant,
                            )
                        }
                    }
                }

                eventLayouts.forEach { layout ->
                    val event = layout.event
                    val eventOffset = (event.startMinute / 60f) * hourHeight.value
                    val eventHeight = (event.durationMinutes / 60f) * hourHeight.value

                    val widthFactor = 1f / layout.totalColumns
                    val xOffset = contentWidth * (layout.columnIndex * widthFactor)

                    Box(
                        modifier = Modifier
                            .width(contentWidth * widthFactor)
                            .padding(horizontal = 2.dp)
                            .offset(x = xOffset, y = eventOffset.dp)
                            .height(eventHeight.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable { onEventClick(event) }
                            .padding(8.dp),
                    ) {
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            maxLines = if (event.durationMinutes < 30) 1 else Int.MAX_VALUE,
                        )
                    }
                }

                if (isToday) {
                    val totalMinutes = currentHour * 60 + currentMinute
                    val verticalOffset = (totalMinutes / 60f) * hourHeight.value

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = verticalOffset.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .offset(x = (-4).dp)
                                .background(Color.White, CircleShape),
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            thickness = 1.dp,
                            color = Color.White,
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(hourHeight))
    }
}

private fun mergeEventsByProfile(events: List<ScheduleEvent>): List<ScheduleEvent> {
    if (events.isEmpty()) return emptyList()

    return events.groupBy { it.profileId }
        .flatMap { (_, profileEvents) ->
            val sorted = profileEvents.sortedBy { it.startMinute }
            val merged = mutableListOf<ScheduleEvent>()

            if (sorted.isEmpty()) return@flatMap emptyList()

            var current = sorted[0]

            for (i in 1 until sorted.size) {
                val next = sorted[i]
                if (next.startMinute <= current.endMinute) {
                    // Overlap or adjacent, merge
                    val newEndTime = if (next.endMinute > current.endMinute) next.endTime else current.endTime
                    current = current.copy(endTime = newEndTime)
                } else {
                    merged.add(current)
                    current = next
                }
            }
            merged.add(current)
            merged
        }
}

private data class EventLayoutInfo(
    val event: ScheduleEvent,
    val columnIndex: Int,
    val totalColumns: Int,
)

private fun computeEventLayouts(events: List<ScheduleEvent>): List<EventLayoutInfo> {
    if (events.isEmpty()) return emptyList()

    // 1. Sort by start time
    val sortedEvents = events.sortedBy { it.startMinute }

    // 2. Group into clusters
    val clusters = mutableListOf<MutableList<ScheduleEvent>>()
    for (event in sortedEvents) {
        val overlappingCluster = clusters.find { cluster ->
            cluster.any { it.startMinute < event.endMinute && event.startMinute < it.endMinute }
        }
        if (overlappingCluster != null) {
            overlappingCluster.add(event)
        } else {
            clusters.add(mutableListOf(event))
        }
    }

    // 3. For each cluster, assign columns
    val result = mutableListOf<EventLayoutInfo>()
    for (cluster in clusters) {
        val columns = mutableListOf<MutableList<ScheduleEvent>>()
        val eventToColumn = mutableMapOf<ScheduleEvent, Int>()

        for (event in cluster.sortedBy { it.startMinute }) {
            var assigned = false
            for (i in columns.indices) {
                val lastInColumn = columns[i].last()
                if (lastInColumn.endMinute <= event.startMinute) {
                    columns[i].add(event)
                    eventToColumn[event] = i
                    assigned = true
                    break
                }
            }
            if (!assigned) {
                columns.add(mutableListOf(event))
                eventToColumn[event] = columns.size - 1
            }
        }

        val totalCols = columns.size
        for (event in cluster) {
            result.add(EventLayoutInfo(event, eventToColumn[event]!!, totalCols))
        }
    }

    return result
}

@Composable
private fun TimeLabel(
    hour: Int,
    height: Dp,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val timeText = remember(hour, context) {
        val time = LocalTime.of(hour, 0)
        val is24Hour = android.text.format.DateFormat.is24HourFormat(context)
        val skeleton = if (is24Hour) "Hm" else "ha"
        val pattern = android.text.format.DateFormat.getBestDateTimePattern(java.util.Locale.getDefault(), skeleton)
        val formatter = java.time.format.DateTimeFormatter.ofPattern(pattern)
        time.format(formatter)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .padding(start = 12.dp),
        contentAlignment = Alignment.TopStart,
    ) {
        Text(
            text = timeText,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ScheduleTimelinePreview() {
    val testEvents = listOf(
        ScheduleEvent(
            id = 1,
            profileId = 1,
            title = "Profile A",
            startTime = LocalTime.of(2, 30),
            endTime = LocalTime.of(7, 0),
        ),
        ScheduleEvent(
            id = 2,
            profileId = 1,
            title = "Profile A",
            startTime = LocalTime.of(4, 0),
            endTime = LocalTime.of(7, 0),
        ),
        ScheduleEvent(
            id = 3,
            profileId = 2,
            title = "Profile B",
            startTime = LocalTime.of(0, 0),
            endTime = LocalTime.of(3, 0),
        ),
        ScheduleEvent(
            id = 4,
            profileId = 2,
            title = "Profile B",
            startTime = LocalTime.of(4, 0),
            endTime = LocalTime.of(4, 30),
        ),
    )

    MaterialTheme(
        colorScheme = androidx.compose.material3.darkColorScheme(),
    ) {
        Surface(color = MaterialTheme.colorScheme.background) {
            ScheduleTimeline(
                events = testEvents,
                dayOfWeek = LocalDate.now().dayOfWeek,
                onHourClick = {},
                onEventClick = {},
            )
        }
    }
}
