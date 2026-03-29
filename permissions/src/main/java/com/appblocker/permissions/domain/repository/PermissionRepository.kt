package com.appblocker.permissions.domain.repository

import com.appblocker.permissions.domain.model.RequiredPermission

interface PermissionRepository {
    fun hasRequiredPermissions(): Boolean
    fun getMissingPermissions(): List<RequiredPermission>
}