package com.howlindev.appblocker.schedule.data.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.howlindev.appblocker.schedule.domain.repository.ScheduleRepository
import com.howlindev.appblocker.schedule.platform.ScheduleReceiver
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class ScheduleAlarmScheduler(
    private val context: Context,
    private val scheduleRepository: ScheduleRepository,
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    suspend fun scheduleNext() {
        val allEvents = scheduleRepository.getAllEvents().first()
        if (allEvents.isEmpty()) return

        val now = LocalDateTime.now()
        var nextTransition: LocalDateTime? = null

        allEvents.forEach { event ->
            // Check today's transitions
            val startToday = now.with(event.startTime).truncatedTo(ChronoUnit.MINUTES)
            val endToday = now.with(event.endTime).truncatedTo(ChronoUnit.MINUTES)

            // If it's an overnight event, handle it
            val realEndToday = if (event.endTime < event.startTime) endToday.plusDays(1) else endToday

            // Potential transitions: start and end of the event on each day it's active
            event.daysOfWeek.forEach { dayOfWeek ->
                val daysUntil = (dayOfWeek.value - now.dayOfWeek.value + 7) % 7
                val nextStart = startToday.plusDays(daysUntil.toLong())
                val nextEnd = realEndToday.plusDays(daysUntil.toLong())

                if (nextStart.isAfter(now)) {
                    if (nextTransition == null || nextStart.isBefore(nextTransition)) {
                        nextTransition = nextStart
                    }
                }
                if (nextEnd.isAfter(now)) {
                    if (nextTransition == null || nextEnd.isBefore(nextTransition)) {
                        nextTransition = nextEnd
                    }
                }
            }
        }

        nextTransition?.let { transition ->
            val intent = Intent(context, ScheduleReceiver::class.java).apply {
                action = ScheduleReceiver.ACTION_UPDATE_SCHEDULE
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

            val triggerAtMillis = transition.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent,
            )
        }
    }
}
