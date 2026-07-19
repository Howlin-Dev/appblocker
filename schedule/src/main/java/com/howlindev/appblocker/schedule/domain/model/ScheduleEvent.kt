package com.howlindev.appblocker.schedule.domain.model

data class ScheduleEvent(
    val id: String,
    val title: String,
    val startMinute: Int,
    val endMinute: Int,
) {
    val durationMinutes: Int get() = endMinute - startMinute
}
