package com.howlindev.appblocker.timer.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.howlindev.appblocker.core.domain.repository.BlockRepository
import com.howlindev.appblocker.timer.data.datastore.TimerDataStore

class TimerWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val blockRepository: BlockRepository,
    private val timerDataStore: TimerDataStore,
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        blockRepository.deactivate()
        timerDataStore.clear()
        return Result.success()
    }
}

