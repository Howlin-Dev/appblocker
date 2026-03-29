package com.appblocker.permissions.presentation

import com.appblocker.permissions.domain.model.RequiredPermission

sealed interface PermissionsAction {
    data class PermissionGrantClick(val permission: RequiredPermission): PermissionsAction
}