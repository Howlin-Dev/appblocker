package com.howlindev.appblocker.schedule.domain.model

import java.time.DayOfWeek

data class TimelineSelection(
    val startMinute: Int,
    val endMinute: Int,
    val dayOfWeek: DayOfWeek,
) {
    val durationMinutes: Int get() = endMinute - startMinute
}
