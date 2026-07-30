package com.howlindev.appblocker.schedule.domain.repository

import com.howlindev.appblocker.schedule.domain.model.ScheduleEvent
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    fun getEventsByProfileId(profileId: Long): Flow<List<ScheduleEvent>>
    fun getAllEvents(): Flow<List<ScheduleEvent>>
    suspend fun saveEvent(event: ScheduleEvent)
    suspend fun deleteEvent(event: ScheduleEvent)
    suspend fun deleteEventsByProfileId(profileId: Long)
}
