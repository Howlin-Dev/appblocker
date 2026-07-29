package com.howlindev.appblocker.schedule.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.howlindev.appblocker.schedule.domain.model.ScheduleEvent
import com.howlindev.appblocker.schedule.domain.model.TimelineSelection
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar

@Composable
fun ScheduleTimeline(
    events: List<ScheduleEvent>,
    dayOfWeek: DayOfWeek,
    onEventClick: (ScheduleEvent) -> Unit,
    modifier: Modifier = Modifier,
    hourHeight: Dp = 80.dp,
    selection: TimelineSelection? = null,
    onSelectionChange: (TimelineSelection?) -> Unit = {},
    onConfirmSelection: () -> Unit = {},
) {
    val density = LocalDensity.current
    val hourHeightPx = with(density) { hourHeight.toPx() }

    val currentSelectionState = rememberUpdatedState(selection)
    val onSelectionChangeState = rememberUpdatedState(onSelectionChange)

    val isTodayInitial = remember(dayOfWeek) { dayOfWeek == LocalDate.now().dayOfWeek }
    val initialScroll = remember(isTodayInitial) {
        if (isTodayInitial) {
            val now = LocalTime.now()
            val totalMinutes = now.hour * 60 + now.minute
            val pos = (totalMinutes / 60f) * hourHeightPx - hourHeightPx
            maxOf(0f, pos).toInt()
        } else 0
    }
    val scrollState = rememberScrollState(initialScroll)

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
            .padding(vertical = 16.dp)
            .pointerInput(Unit) {
                detectTapGestures {
                    onSelectionChange(null)
                }
            },
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
                    .height(hourHeight * 24)
                    .pointerInput(dayOfWeek) {
                        detectTapGestures { offset ->
                            val totalMinutes = ((offset.y / hourHeightPx) * 60).toInt()
                            val hour = totalMinutes / 60
                            val minute = (totalMinutes % 60 / 15) * 15 // Snap to 15 mins
                            onSelectionChange(
                                TimelineSelection(
                                    startMinute = hour * 60 + minute,
                                    endMinute = hour * 60 + minute + 60,
                                    dayOfWeek = dayOfWeek,
                                ),
                            )
                        }
                    },
            ) {
                val contentWidth = maxWidth

                TimelineGrid(hourHeight = hourHeight)

                TimelineEvents(
                    eventLayouts = eventLayouts,
                    hourHeight = hourHeight,
                    contentWidth = contentWidth,
                    onEventClick = onEventClick,
                )

                // Selection Layer
                if (selection != null && selection.dayOfWeek == dayOfWeek) {
                    TimelineSelectionBox(
                        selection = selection,
                        hourHeightPx = hourHeightPx,
                        currentSelectionState = currentSelectionState,
                        onSelectionChangeState = onSelectionChangeState,
                        onConfirmSelection = onConfirmSelection,
                    )
                }

                if (isToday) {
                    CurrentTimeIndicator(
                        currentHour = currentHour,
                        currentMinute = currentMinute,
                        hourHeight = hourHeight,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(hourHeight))
    }
}

@Composable
private fun TimelineGrid(hourHeight: Dp) {
    Column {
        (0..23).forEach { _ ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(hourHeight),
            ) {
                HorizontalDivider(
                    modifier = Modifier.align(Alignment.TopStart),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
            }
        }
    }
}

@Composable
private fun TimelineEvents(
    eventLayouts: List<EventLayoutInfo>,
    hourHeight: Dp,
    contentWidth: Dp,
    onEventClick: (ScheduleEvent) -> Unit,
) {
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
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .clickable { onEventClick(event) }
                .padding(8.dp),
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Bold,
                maxLines = if (event.durationMinutes < 30) 1 else Int.MAX_VALUE,
            )
        }
    }
}

@Composable
private fun TimelineSelectionBox(
    selection: TimelineSelection,
    hourHeightPx: Float,
    currentSelectionState: androidx.compose.runtime.State<TimelineSelection?>,
    onSelectionChangeState: androidx.compose.runtime.State<(TimelineSelection?) -> Unit>,
    onConfirmSelection: () -> Unit,
    color: Color = MaterialTheme.colorScheme.tertiary,
) {
    val density = LocalDensity.current
    val selectionOffset = (selection.startMinute / 60f) * hourHeightPx
    val selectionHeight = (selection.durationMinutes / 60f) * hourHeightPx

    var dragInitialSelection by remember { mutableStateOf<TimelineSelection?>(null) }
    var dragTotalDeltaY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = with(density) { selectionOffset.toDp() })
            .height(with(density) { selectionHeight.toDp() })
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        dragInitialSelection = currentSelectionState.value
                        dragTotalDeltaY = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragTotalDeltaY += dragAmount.y
                        val initial = dragInitialSelection ?: return@detectDragGestures
                        val deltaMinutes = ((dragTotalDeltaY / hourHeightPx) * 60).toInt()
                        val newStart = (initial.startMinute + deltaMinutes)
                            .coerceIn(0, 1440 - initial.durationMinutes)
                        val snappedStart = (newStart / 15) * 15
                        onSelectionChangeState.value(
                            initial.copy(
                                startMinute = snappedStart,
                                endMinute = snappedStart + initial.durationMinutes,
                            ),
                        )
                    },
                )
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { return@clickable },
            )
            .padding(4.dp),
    ) {
        // Outline
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.1f))
                .clickable(
                    onClick = onConfirmSelection,
                )
                .border(2.dp, color, RoundedCornerShape(8.dp)),
        ) {}

        // Top Handle
        Box(
            modifier = Modifier
                .size(16.dp)
                .offset(x = 16.dp, y = (-8).dp)
                .background(color, CircleShape)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            dragInitialSelection = currentSelectionState.value
                            dragTotalDeltaY = 0f
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            dragTotalDeltaY += dragAmount.y
                            val initial = dragInitialSelection ?: return@detectDragGestures
                            val deltaMinutes = ((dragTotalDeltaY / hourHeightPx) * 60).toInt()
                            val newStart = (initial.startMinute + deltaMinutes)
                                .coerceIn(0, initial.endMinute - 15)
                            val snappedStart = (newStart / 15) * 15
                            onSelectionChangeState.value(
                                initial.copy(startMinute = snappedStart),
                            )
                        },
                    )
                }
                .align(Alignment.TopStart),
        )

        // Bottom Handle
        Box(
            modifier = Modifier
                .size(16.dp)
                .offset(x = (-16).dp, y = 8.dp)
                .background(color, CircleShape)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            dragInitialSelection = currentSelectionState.value
                            dragTotalDeltaY = 0f
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            dragTotalDeltaY += dragAmount.y
                            val initial = dragInitialSelection ?: return@detectDragGestures
                            val deltaMinutes = ((dragTotalDeltaY / hourHeightPx) * 60).toInt()
                            val newEnd = (initial.endMinute + deltaMinutes)
                                .coerceIn(initial.startMinute + 15, 1440)
                            val snappedEnd = (newEnd / 15) * 15
                            onSelectionChangeState.value(
                                initial.copy(endMinute = snappedEnd),
                            )
                        },
                    )
                }
                .align(Alignment.BottomEnd),
        )
    }
}

@Composable
private fun CurrentTimeIndicator(
    currentHour: Int,
    currentMinute: Int,
    hourHeight: Dp,
) {
    val totalMinutes = currentHour * 60 + currentMinute
    val verticalOffset = (totalMinutes / 60f) * hourHeight.value

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = verticalOffset.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .offset(x = (-6).dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
        )
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.primary,
        )
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
                onEventClick = {},
            )
        }
    }
}
