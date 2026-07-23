package com.howlindev.appblocker.schedule.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.howlindev.appblocker.core.domain.model.Profile
import com.howlindev.appblocker.core.domain.repository.ProfilesRepository
import com.howlindev.appblocker.schedule.domain.model.ScheduleEvent
import com.howlindev.appblocker.schedule.domain.usecase.GetAllScheduleEventsUseCase
import com.howlindev.appblocker.schedule.domain.usecase.SaveScheduleEventUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalTime

class SchedulerViewModel(
    getAllScheduleEventsUseCase: GetAllScheduleEventsUseCase,
    private val saveScheduleEventUseCase: SaveScheduleEventUseCase,
    profilesRepository: ProfilesRepository,
) : ViewModel() {

    private val _isScheduleDialogOpen = MutableStateFlow(false)
    private val _draftSchedule = MutableStateFlow<DraftSchedule?>(null)
    private val _selectedProfileId = MutableStateFlow<Long?>(null)

    val state: StateFlow<SchedulerState> = combine(
        getAllScheduleEventsUseCase(),
        profilesRepository.getAll(),
        _isScheduleDialogOpen,
        _draftSchedule,
        _selectedProfileId,
    ) { events, profiles, isOpen, draft, selectedId ->
        val profileNames = profiles.associate { it.id to it.name }
        val eventsWithTitles = events.map { event ->
            event.copy(title = profileNames[event.profileId] ?: "Unknown")
        }

        val eventsByDay = DayOfWeek.entries.associateWith { day ->
            eventsWithTitles.filter { it.daysOfWeek.contains(day) }
        }

        SchedulerState(
            eventsByDay = eventsByDay,
            profiles = profiles,
            isScheduleDialogOpen = isOpen,
            draftSchedule = draft,
            selectedProfileId = selectedId,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SchedulerState(),
    )

    fun onAction(action: SchedulerAction) {
        when (action) {
            is SchedulerAction.ShowScheduleDialog -> {
                _draftSchedule.value = DraftSchedule(
                    from = LocalTime.of(action.hour, 0),
                    until = LocalTime.of((action.hour + 1) % 24, 0),
                    days = setOf(action.day),
                )
                _isScheduleDialogOpen.value = true
            }

            SchedulerAction.DismissScheduleDialog -> {
                _isScheduleDialogOpen.value = false
                _draftSchedule.value = null
                _selectedProfileId.value = null
            }

            is SchedulerAction.ProfileSelected -> {
                _selectedProfileId.value = action.profileId
            }

            is SchedulerAction.ConfirmSchedule -> {
                val profileId = _selectedProfileId.value ?: return
                viewModelScope.launch {
                    saveScheduleEventUseCase(
                        ScheduleEvent(
                            profileId = profileId,
                            startTime = action.from,
                            endTime = action.until,
                            daysOfWeek = action.days,
                        ),
                    )
                    onAction(SchedulerAction.DismissScheduleDialog)
                }
            }

            is SchedulerAction.ProfileCreated -> {
                _selectedProfileId.value = action.profileId
                _isScheduleDialogOpen.value = true
            }

        }
    }
}

data class SchedulerState(
    val eventsByDay: Map<DayOfWeek, List<ScheduleEvent>> = emptyMap(),
    val profiles: List<Profile> = emptyList(),
    val isScheduleDialogOpen: Boolean = false,
    val draftSchedule: DraftSchedule? = null,
    val selectedProfileId: Long? = null,
)

data class DraftSchedule(
    val from: LocalTime,
    val until: LocalTime,
    val days: Set<DayOfWeek>,
)

sealed interface SchedulerAction {
    data class ShowScheduleDialog(val hour: Int, val day: DayOfWeek) : SchedulerAction
    data object DismissScheduleDialog : SchedulerAction
    data class ProfileSelected(val profileId: Long) : SchedulerAction
    data class ConfirmSchedule(val from: LocalTime, val until: LocalTime, val days: Set<DayOfWeek>) : SchedulerAction
    data class ProfileCreated(val profileId: Long) : SchedulerAction
}
