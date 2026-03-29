package com.serhii.appblocker.presentation.home

import androidx.lifecycle.ViewModel
import com.appblocker.permissions.domain.repository.PermissionRepository
import com.appblocker.permissions.platform.requester.PermissionRequester

class HomeViewModel(
    private val permissionRepository: PermissionRepository,
    private val permissionRequester: PermissionRequester,
) : ViewModel() {

    init {
        checkAndRequestPermissions()
    }

    fun checkAndRequestPermissions() {
        val missingPermissions = permissionRepository.getMissingPermissions()
        missingPermissions.forEach { permission ->
            permissionRequester.request(permission)
        }
    }
}
