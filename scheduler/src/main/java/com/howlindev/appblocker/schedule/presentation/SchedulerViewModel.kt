package com.howlindev.appblocker.schedule.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.howlindev.appblocker.core.domain.model.Profile
import com.howlindev.appblocker.core.domain.repository.ProfilesRepository
import com.howlindev.appblocker.schedule.domain.model.ScheduleEvent
import com.howlindev.appblocker.schedule.domain.model.TimelineSelection
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
    private val _currentSelection = MutableStateFlow<TimelineSelection?>(null)

    val state: StateFlow<SchedulerState> = combine(
        getAllScheduleEventsUseCase(),
        profilesRepository.getAll(),
        _isScheduleDialogOpen,
        _draftSchedule,
        _selectedProfileId,
        _currentSelection,
    ) { args ->
        val events = args[0] as List<ScheduleEvent>
        val profiles = args[1] as List<Profile>
        val isOpen = args[2] as Boolean
        val draft = args[3] as DraftSchedule?
        val selectedId = args[4] as Long?
        val selection = args[5] as TimelineSelection?

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
            currentSelection = selection,
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
                    _currentSelection.value = null
                }
            }

            is SchedulerAction.ProfileCreated -> {
                _selectedProfileId.value = action.profileId
                _isScheduleDialogOpen.value = true
            }

            is SchedulerAction.StartSelection -> {
                val startMinute = action.hour * 60 + action.minute
                _currentSelection.value = TimelineSelection(
                    startMinute = startMinute,
                    endMinute = startMinute + 60,
                    dayOfWeek = action.day,
                )
            }

            is SchedulerAction.UpdateSelection -> {
                _currentSelection.value = _currentSelection.value?.copy(
                    startMinute = action.startMinute,
                    endMinute = action.endMinute,
                )
            }

            SchedulerAction.ClearSelection -> {
                _currentSelection.value = null
            }

            SchedulerAction.CreateScheduleFromSelection -> {
                val selection = _currentSelection.value ?: return
                val startHour = selection.startMinute / 60
                val startMinute = selection.startMinute % 60
                val endHour = (selection.endMinute / 60) % 24
                val endMinute = selection.endMinute % 60

                _draftSchedule.value = DraftSchedule(
                    from = LocalTime.of(startHour, startMinute),
                    until = LocalTime.of(endHour, endMinute),
                    days = setOf(selection.dayOfWeek),
                )
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
    val currentSelection: TimelineSelection? = null,
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
    data class StartSelection(val hour: Int, val minute: Int, val day: DayOfWeek) : SchedulerAction
    data class UpdateSelection(val startMinute: Int, val endMinute: Int) : SchedulerAction
    data object ClearSelection : SchedulerAction
    data object CreateScheduleFromSelection : SchedulerAction
}
