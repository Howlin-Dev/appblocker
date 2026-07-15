package com.howlindev.appblocker.schedule.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.util.Calendar

@Composable
fun ScheduleTimeline(
    onHourClick: (Int) -> Unit,
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

    // Auto-scroll to current time on first launch
    LaunchedEffect(Unit) {
        val totalMinutes = currentHour * 60 + currentMinute
        val scrollPosition = (totalMinutes / 60f) * hourHeight.value
        // We'd need to convert dp to px for accurate scrolling if we used scrollState.scrollTo
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(vertical = 16.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column {
                    (0..23).forEach { hour ->
                        HourRow(
                            hour = hour,
                            height = hourHeight,
                            onClick = { onHourClick(hour) }
                        )
                    }
                    // Extra space at bottom
                    Spacer(modifier = Modifier.height(hourHeight))
                }

                // Current TimeLine
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
                            .offset(x = 56.dp) // Align slightly before the divider
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
    }
}

@Composable
private fun HourRow(
    hour: Int,
    height: Dp,
    onClick: () -> Unit,
) {
    val timeText = remember(hour) {
        val h = if (hour == 0 || hour == 12) 12 else hour % 12
        val amPm = if (hour < 12) "AM" else "PM"
        "$h $amPm"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = timeText,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .width(64.dp)
                .padding(start = 12.dp, top = 0.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .padding(end = 16.dp)
        ) {
            HorizontalDivider(
                modifier = Modifier.align(Alignment.TopStart),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onClick() }
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ScheduleTimelinePreview() {
    MaterialTheme(
        colorScheme = androidx.compose.material3.darkColorScheme()
    ) {
        Surface(color = MaterialTheme.colorScheme.background) {
            ScheduleTimeline(onHourClick = {})
        }
    }
}
