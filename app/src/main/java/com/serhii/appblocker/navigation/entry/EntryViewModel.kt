package com.serhii.appblocker.navigation.entry

import androidx.lifecycle.ViewModel
import com.appblocker.permissions.domain.usecase.GetMissingPermissionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EntryViewModel(
    private val getMissingPermissionsUseCase: GetMissingPermissionsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(EntryState())
    val state: StateFlow<EntryState> = _state.asStateFlow()

    init {
        checkPermissions()
    }

    fun checkPermissions() {
        val arePermissionsNeeded = getMissingPermissionsUseCase().isNotEmpty()
        _state.update { it.copy(arePermissionsNeeded = arePermissionsNeeded) }
    }
}

data class EntryState(
    val arePermissionsNeeded: Boolean? = null,
)
