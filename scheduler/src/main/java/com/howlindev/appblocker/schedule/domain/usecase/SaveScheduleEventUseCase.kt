package com.howlindev.appblocker.schedule.domain.usecase

import com.howlindev.appblocker.schedule.data.alarm.ScheduleAlarmScheduler
import com.howlindev.appblocker.schedule.domain.model.ScheduleEvent
import com.howlindev.appblocker.schedule.domain.repository.ScheduleRepository

class SaveScheduleEventUseCase(
    private val repository: ScheduleRepository,
    private val updateScheduleBlockingUseCase: UpdateScheduleBlockingUseCase,
    private val scheduleAlarmScheduler: ScheduleAlarmScheduler,
) {
    suspend operator fun invoke(event: ScheduleEvent) {
        repository.saveEvent(event)
        updateScheduleBlockingUseCase()
        scheduleAlarmScheduler.scheduleNext()
    }
}
