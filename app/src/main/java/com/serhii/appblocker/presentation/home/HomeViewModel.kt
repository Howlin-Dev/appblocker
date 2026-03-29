package com.serhii.appblocker.presentation.home

import androidx.lifecycle.ViewModel
import com.serhii.appblocker.domain.repository.PermissionRepository
import com.serhii.appblocker.platform.requester.PermissionRequester

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
