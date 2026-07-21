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
import java.util.Calendar

@Composable
fun ScheduleTimeline(
    events: List<ScheduleEvent>,
    onHourClick: (Int) -> Unit,
    onEventClick: (ScheduleEvent) -> Unit,
    modifier: Modifier = Modifier,
    hourHeight: Dp = 80.dp,
) {
    val scrollState = rememberScrollState()

    // Get current time using Calendar for API 24 compatibility
    var currentHour by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) }
    var currentMinute by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.MINUTE)) }

    LaunchedEffect(Unit) {
        while (true) {
            val calendar = Calendar.getInstance()
            currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            currentMinute = calendar.get(Calendar.MINUTE)
            delay(60000) // Update every minute
        }
    }

    val eventLayouts = remember(events) { computeEventLayouts(events) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(vertical = 16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Time Labels Column
            Column(modifier = Modifier.width(64.dp)) {
                (0..23).forEach { hour ->
                    TimeLabel(hour = hour, height = hourHeight)
                }
            }

            // Timeline Content Area
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .height(hourHeight * 24)
            ) {
                val contentWidth = maxWidth

                // Background Grid
                Column {
                    (0..23).forEach { hour ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(hourHeight)
                                .clickable { onHourClick(hour) }
                        ) {
                            HorizontalDivider(
                                modifier = Modifier.align(Alignment.TopStart),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                }

                // Events
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
                            .padding(8.dp)
                    ) {
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            maxLines = if (event.durationMinutes < 30) 1 else Int.MAX_VALUE
                        )
                    }
                }

                // Current Time Line
                val totalMinutes = currentHour * 60 + currentMinute
                val verticalOffset = (totalMinutes / 60f) * hourHeight.value

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = verticalOffset.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .offset(x = (-4).dp) // Half of size to center on the start of content area
                            .background(Color.White, CircleShape)
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        thickness = 1.dp,
                        color = Color.White
                    )
                }
            }
        }
        
        // Extra space at bottom for scrolling
        Spacer(modifier = Modifier.height(hourHeight))
    }
}

private data class EventLayoutInfo(
    val event: ScheduleEvent,
    val columnIndex: Int,
    val totalColumns: Int
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
    val timeText = remember(hour) {
        val h = if (hour == 0 || hour == 12) 12 else hour % 12
        val amPm = if (hour < 12) "AM" else "PM"
        "$h $amPm"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .padding(start = 12.dp),
        contentAlignment = Alignment.TopStart
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
            id = "0",
            title = "3-Hour Event",
            startMinute = 120, // 2:00 AM
            endMinute = 300    // 5:00 AM
        ),
        ScheduleEvent(
            id = "overlap",
            title = "Overlap Meeting",
            startMinute = 180, // 3:00 AM
            endMinute = 240    // 4:00 AM
        ),
        ScheduleEvent(
            id = "1",
            title = "Long Meeting",
            startMinute = 600, // 10:00 AM
            endMinute = 780    // 1:00 PM (3 hours)
        ),
        ScheduleEvent(
            id = "2",
            title = "Quick Break",
            startMinute = 840, // 2:00 PM
            endMinute = 870    // 2:30 PM
        )
    )

    MaterialTheme(
        colorScheme = androidx.compose.material3.darkColorScheme()
    ) {
        Surface(color = MaterialTheme.colorScheme.background) {
            ScheduleTimeline(
                events = testEvents,
                onHourClick = {},
                onEventClick = {}
            )
        }
    }
}
