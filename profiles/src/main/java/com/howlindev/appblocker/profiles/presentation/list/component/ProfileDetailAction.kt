package com.howlindev.appblocker.profiles.presentation.list.component

import com.howlindev.appblocker.schedule.domain.model.ScheduleEvent
import java.time.DayOfWeek
import java.time.LocalTime

sealed interface ProfileDetailAction {
    data object BackClick : ProfileDetailAction
    data object DeleteProfile : ProfileDetailAction
    data object ManageAppListClick : ProfileDetailAction
    data object ManageWebsiteListClick : ProfileDetailAction
    data class ProfileNameChanged(val name: String) : ProfileDetailAction
    data class SaveScheduleEvent(
        val from: LocalTime,
        val until: LocalTime,
        val days: Set<DayOfWeek>,
        val eventId: Long = 0,
    ) : ProfileDetailAction
    data class DeleteScheduleEvent(val event: ScheduleEvent) : ProfileDetailAction
    data class DuplicateProfileConfirmed(val name: String) : ProfileDetailAction
}
