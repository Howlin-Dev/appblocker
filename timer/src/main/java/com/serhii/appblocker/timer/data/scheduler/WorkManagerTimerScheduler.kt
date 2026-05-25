package com.serhii.appblocker.timer.data.scheduler

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.serhii.appblocker.timer.data.worker.TimerWorker
import java.util.concurrent.TimeUnit

class WorkManagerTimerScheduler(
    private val context: Context,
) : TimerScheduler {

    override fun schedule(triggerAtMillis: Long) {
        val delay = triggerAtMillis - System.currentTimeMillis().coerceAtLeast(0)

        val request = OneTimeWorkRequestBuilder<TimerWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag(WORK_TAG)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                request,
            )
    }

    override fun cancel() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    companion object {
        const val WORK_NAME = "timer_work"
        const val WORK_TAG = "timer_tag"
    }
}
