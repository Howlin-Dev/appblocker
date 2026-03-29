package com.serhii.appblocker.presentation.permissions

import com.serhii.appblocker.domain.model.RequiredPermission

sealed interface PermissionsAction {
    data class PermissionGrantClick(val permission: RequiredPermission): PermissionsAction
}