package com.serhii.appblocker.timer.data.scheduler

interface TimerScheduler {
    fun schedule(triggerAtMillis: Long)
    fun cancel()
}
