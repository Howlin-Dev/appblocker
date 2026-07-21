package com.howlindev.appblocker.schedule.presentation

import androidx.lifecycle.ViewModel
import com.howlindev.appblocker.schedule.domain.model.ScheduleEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.DayOfWeek

class SchedulerViewModel : ViewModel() {
    private val _state = MutableStateFlow(SchedulerState())
    val state = _state.asStateFlow()

    fun onAction(action: SchedulerAction) {
        // Handle actions here
    }
}

data class SchedulerState(
    val eventsByDay: Map<DayOfWeek, List<ScheduleEvent>> = emptyMap()
)

sealed interface SchedulerAction {
    data class DaySelected(val day: DayOfWeek) : SchedulerAction
}
