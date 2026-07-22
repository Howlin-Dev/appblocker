package com.howlindev.appblocker.schedule.domain.usecase

import com.howlindev.appblocker.schedule.domain.model.ScheduleEvent
import com.howlindev.appblocker.schedule.domain.repository.ScheduleRepository

class SaveScheduleEventUseCase(
    private val repository: ScheduleRepository,
) {
    suspend operator fun invoke(event: ScheduleEvent) {
        repository.saveEvent(event)
    }
}
