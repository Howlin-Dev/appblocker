package com.howlindev.appblocker.schedule.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.howlindev.appblocker.schedule.data.alarm.ScheduleAlarmScheduler
import com.howlindev.appblocker.schedule.domain.usecase.UpdateScheduleBlockingUseCase

class ScheduleWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val updateScheduleBlockingUseCase: UpdateScheduleBlockingUseCase,
    private val scheduleAlarmScheduler: ScheduleAlarmScheduler,
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        updateScheduleBlockingUseCase()
        scheduleAlarmScheduler.scheduleNext()
        return Result.success()
    }
}
