package com.howlindev.appblocker.schedule.platform

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.howlindev.appblocker.schedule.data.worker.ScheduleWorker

class ScheduleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            ACTION_UPDATE_SCHEDULE,
            -> {
                val workRequest = OneTimeWorkRequestBuilder<ScheduleWorker>().build()
                WorkManager.getInstance(context).enqueue(workRequest)
            }
        }
    }

    companion object {
        const val ACTION_UPDATE_SCHEDULE = "com.howlindev.appblocker.ACTION_UPDATE_SCHEDULE"
    }
}
