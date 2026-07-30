package com.howlindev.appblocker.schedule.domain.usecase

import com.howlindev.appblocker.schedule.domain.model.ScheduleEvent
import com.howlindev.appblocker.schedule.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow

class GetAllScheduleEventsUseCase(
    private val scheduleRepository: ScheduleRepository,
) {
    operator fun invoke(): Flow<List<ScheduleEvent>> = scheduleRepository.getAllEvents()
}
