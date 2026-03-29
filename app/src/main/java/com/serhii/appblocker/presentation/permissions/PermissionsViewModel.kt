package com.serhii.appblocker.presentation.permissions

import androidx.lifecycle.ViewModel
import com.serhii.appblocker.domain.model.RequiredPermission
import com.serhii.appblocker.domain.repository.PermissionRepository
import com.serhii.appblocker.platform.requester.PermissionRequester
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PermissionsViewModel(
    private val permissionRepository: PermissionRepository,
    private val permissionRequester: PermissionRequester,
) : ViewModel() {

    private val _state = MutableStateFlow(PermissionsState())
    val state: StateFlow<PermissionsState> = _state.asStateFlow()

    fun checkPermissions() {
        val missingPermissions = permissionRepository.getMissingPermissions()
        _state.update {
            _state.value.copy(
                missingRequiredPermissions = missingPermissions,
                allPermissionsGranted = missingPermissions.isEmpty()
            )
        }
    }

    fun requestPermission(permission: RequiredPermission) {
        permissionRequester.request(permission)
    }
}

data class PermissionsState(
    val missingRequiredPermissions: List<RequiredPermission> = emptyList(),
    val allPermissionsGranted: Boolean = false,
)