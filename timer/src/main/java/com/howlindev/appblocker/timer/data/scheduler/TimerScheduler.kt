package com.howlindev.appblocker.timer.data.scheduler

interface TimerScheduler {
    fun schedule(triggerAtMillis: Long)
    fun cancel()
}

