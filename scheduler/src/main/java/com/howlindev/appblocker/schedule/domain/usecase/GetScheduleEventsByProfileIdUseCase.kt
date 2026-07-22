package com.howlindev.appblocker.schedule.domain.usecase

import com.howlindev.appblocker.schedule.domain.model.ScheduleEvent
import com.howlindev.appblocker.schedule.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow

class GetScheduleEventsByProfileIdUseCase(
    private val repository: ScheduleRepository,
) {
    operator fun invoke(profileId: Long): Flow<List<ScheduleEvent>> {
        return repository.getEventsByProfileId(profileId)
    }
}
