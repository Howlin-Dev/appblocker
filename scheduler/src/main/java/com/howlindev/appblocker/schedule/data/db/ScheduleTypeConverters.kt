package com.howlindev.appblocker.schedule.data.db

import androidx.room.TypeConverter
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ScheduleTypeConverters {
    private val timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME

    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? {
        return time?.format(timeFormatter)
    }

    @TypeConverter
    fun toLocalTime(timeString: String?): LocalTime? {
        return timeString?.let { LocalTime.parse(it, timeFormatter) }
    }

    @TypeConverter
    fun fromDaysOfWeek(days: Set<DayOfWeek>?): String? {
        return days?.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toDaysOfWeek(daysString: String?): Set<DayOfWeek>? {
        if (daysString == null || daysString.isEmpty()) return emptySet()
        return daysString.split(",").map { DayOfWeek.valueOf(it) }.toSet()
    }
}
