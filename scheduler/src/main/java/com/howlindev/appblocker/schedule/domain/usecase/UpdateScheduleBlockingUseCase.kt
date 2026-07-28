package com.howlindev.appblocker.schedule.domain.usecase

import com.howlindev.appblocker.core.domain.repository.BlockRepository
import com.howlindev.appblocker.core.domain.repository.ProfilesRepository
import com.howlindev.appblocker.schedule.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.LocalTime

class UpdateScheduleBlockingUseCase(
    private val scheduleRepository: ScheduleRepository,
    private val profilesRepository: ProfilesRepository,
    private val blockRepository: BlockRepository,
) {
    suspend operator fun invoke() {
        val now = LocalDateTime.now()
        val currentDay = now.dayOfWeek
        val currentTime = now.toLocalTime()

        val allEvents = scheduleRepository.getAllEvents().first()
        val activeEvents = allEvents.filter { event ->
            currentDay in event.daysOfWeek && isTimeInRange(currentTime, event.startTime, event.endTime)
        }

        if (activeEvents.isEmpty()) {
            blockRepository.deactivateScheduled()
            return
        }

        val profileIds = activeEvents.map { it.profileId }.distinct()
        val allProfiles = profilesRepository.getAll().first()
        val activeProfiles = allProfiles.filter { it.id in profileIds }

        val profileEndTimes = activeEvents.groupBy { it.profileId }.mapValues { (_, events) ->
            val maxEndTime = events.map { it.endTime }.maxWithOrNull { t1, t2 ->
                when {
                    t1 == t2 -> 0
                    t1 == LocalTime.MIDNIGHT -> 1
                    t2 == LocalTime.MIDNIGHT -> -1
                    else -> t1.compareTo(t2)
                }
            } ?: LocalTime.MIDNIGHT
            maxEndTime.toString()
        }

        val mergedPackages = activeProfiles.flatMap { it.appPackages }.distinct()
        val mergedWebsites = activeProfiles.flatMap { it.blockedWebsites }.distinct()

        blockRepository.activateScheduledBlocking(mergedPackages, mergedWebsites, profileEndTimes)
    }

    private fun isTimeInRange(current: LocalTime, start: LocalTime, end: LocalTime): Boolean {
        if (start == LocalTime.MIDNIGHT && end == LocalTime.MIDNIGHT) return true
        return if (start <= end) {
            current in start..end
        } else {
            // Overnight schedule (e.g., 22:00 to 02:00)
            current >= start || current <= end
        }
    }
}
