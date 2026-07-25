package com.howlindev.appblocker.schedule.domain.model

import java.time.DayOfWeek
import java.time.LocalTime

data class ScheduleEvent(
    val id: Long = 0,
    val profileId: Long = 0,
    val startTime: LocalTime = LocalTime.of(9, 0),
    val endTime: LocalTime = LocalTime.of(17, 0),
    val daysOfWeek: Set<DayOfWeek> = emptySet(),
    val title: String = "",
) {
    val startMinute: Int get() = startTime.hour * 60 + startTime.minute
    val endMinute: Int get() = endTime.hour * 60 + endTime.minute
    val durationMinutes: Int get() = endMinute - startMinute

    fun isActiveNow(now: java.time.LocalDateTime = java.time.LocalDateTime.now()): Boolean {
        val currentDay = now.dayOfWeek
        val currentTime = now.toLocalTime()

        if (currentDay !in daysOfWeek) return false

        return if (startTime <= endTime) {
            currentTime in startTime..endTime
        } else {
            // Overnight schedule
            currentTime >= startTime || currentTime <= endTime
        }
    }
}
