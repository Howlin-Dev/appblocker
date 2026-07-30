package com.howlindev.appblocker.schedule.data.repository

import com.howlindev.appblocker.schedule.data.db.ScheduleEventDao
import com.howlindev.appblocker.schedule.data.model.toDomain
import com.howlindev.appblocker.schedule.data.model.toEntity
import com.howlindev.appblocker.schedule.domain.model.ScheduleEvent
import com.howlindev.appblocker.schedule.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ScheduleRepositoryImpl(
    private val dao: ScheduleEventDao,
) : ScheduleRepository {
    override fun getEventsByProfileId(profileId: Long): Flow<List<ScheduleEvent>> {
        return dao.getEventsByProfileId(profileId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllEvents(): Flow<List<ScheduleEvent>> {
        return dao.getAllEvents().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveEvent(event: ScheduleEvent) {
        dao.insertEvent(event.toEntity())
    }

    override suspend fun deleteEvent(event: ScheduleEvent) {
        dao.deleteEvent(event.toEntity())
    }

    override suspend fun deleteEventsByProfileId(profileId: Long) {
        dao.deleteEventsByProfileId(profileId)
    }
}
