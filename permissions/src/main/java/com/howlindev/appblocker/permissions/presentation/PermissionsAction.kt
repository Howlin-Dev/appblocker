package com.howlindev.appblocker.permissions.presentation

import com.howlindev.appblocker.permissions.domain.model.RequiredPermission

sealed interface PermissionsAction {
    data class PermissionGrantClick(val permission: RequiredPermission) : PermissionsAction
}

