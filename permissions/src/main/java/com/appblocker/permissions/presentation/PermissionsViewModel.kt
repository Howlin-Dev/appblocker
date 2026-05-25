package com.appblocker.permissions.presentation

import androidx.lifecycle.ViewModel
import com.appblocker.permissions.domain.model.RequiredPermission
import com.appblocker.permissions.domain.usecase.GetMissingPermissionsUseCase
import com.appblocker.permissions.domain.usecase.RequestPermissionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PermissionsViewModel(
    private val getMissingPermissionsUseCase: GetMissingPermissionsUseCase,
    private val requestPermissionUseCase: RequestPermissionUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(PermissionsState())
    val state: StateFlow<PermissionsState> = _state.asStateFlow()

    fun checkPermissions() {
        val missingPermissions = getMissingPermissionsUseCase()
        _state.update {
            _state.value.copy(
                missingRequiredPermissions = missingPermissions,
                allPermissionsGranted = missingPermissions.isEmpty()
            )
        }
    }

    fun requestPermission(permission: RequiredPermission) {
        requestPermissionUseCase(permission)
    }
}

data class PermissionsState(
    val missingRequiredPermissions: List<RequiredPermission> = emptyList(),
    val allPermissionsGranted: Boolean = false,
)