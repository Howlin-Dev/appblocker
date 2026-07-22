package com.howlindev.appblocker.schedule.domain.usecase

import com.howlindev.appblocker.schedule.domain.model.ScheduleEvent
import com.howlindev.appblocker.schedule.domain.repository.ScheduleRepository

class DeleteScheduleEventUseCase(
    private val repository: ScheduleRepository,
) {
    suspend operator fun invoke(event: ScheduleEvent) {
        repository.deleteEvent(event)
    }
}
