package com.serhii.appblocker.navigation.entry

import androidx.lifecycle.ViewModel
import com.appblocker.permissions.domain.repository.PermissionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EntryViewModel(
    private val permissionRepository: PermissionRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(EntryState())
    val state: StateFlow<EntryState> = _state.asStateFlow()

    init {
        checkPermissions()
    }

    fun checkPermissions() {
        val missingPermissions = permissionRepository.getMissingPermissions()
        _state.update { it.copy(arePermissionsNeeded = missingPermissions.isNotEmpty()) }
    }
}

data class EntryState(
    val arePermissionsNeeded: Boolean? = null
)