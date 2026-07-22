package com.howlindev.appblocker.schedule.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.howlindev.appblocker.schedule.domain.model.ScheduleEvent
import java.time.DayOfWeek
import java.time.LocalTime

@Entity(tableName = "schedule_events")
data class ScheduleEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val profileId: Long,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val daysOfWeek: Set<DayOfWeek>,
)

fun ScheduleEventEntity.toDomain(): ScheduleEvent {
    return ScheduleEvent(
        id = id,
        profileId = profileId,
        startTime = startTime,
        endTime = endTime,
        daysOfWeek = daysOfWeek,
    )
}

fun ScheduleEvent.toEntity(): ScheduleEventEntity {
    return ScheduleEventEntity(
        id = id,
        profileId = profileId,
        startTime = startTime,
        endTime = endTime,
        daysOfWeek = daysOfWeek,
    )
}
