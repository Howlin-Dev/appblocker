package com.howlindev.appblocker.schedule.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.howlindev.appblocker.core.domain.repository.ProfilesRepository
import com.howlindev.appblocker.schedule.domain.model.ScheduleEvent
import com.howlindev.appblocker.schedule.domain.usecase.GetAllScheduleEventsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek

class SchedulerViewModel(
    getAllScheduleEventsUseCase: GetAllScheduleEventsUseCase,
    profilesRepository: ProfilesRepository,
) : ViewModel() {
    val state: StateFlow<SchedulerState> = combine(
        getAllScheduleEventsUseCase(),
        profilesRepository.getAll(),
    ) { events, profiles ->
        val profileNames = profiles.associate { it.id to it.name }
        val eventsWithTitles = events.map { event ->
            event.copy(title = profileNames[event.profileId] ?: "Unknown")
        }

        val eventsByDay = DayOfWeek.entries.associateWith { day ->
            eventsWithTitles.filter { it.daysOfWeek.contains(day) }
        }

        SchedulerState(eventsByDay = eventsByDay)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SchedulerState(),
    )

    fun onAction(action: SchedulerAction) {
        // Handle actions here
    }
}

data class SchedulerState(
    val eventsByDay: Map<DayOfWeek, List<ScheduleEvent>> = emptyMap(),
)

sealed interface SchedulerAction {
    data class DaySelected(val day: DayOfWeek) : SchedulerAction
}
