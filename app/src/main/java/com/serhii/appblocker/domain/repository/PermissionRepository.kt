package com.serhii.appblocker.domain.repository

import com.serhii.appblocker.domain.model.RequiredPermission

interface PermissionRepository {
    fun hasRequiredPermissions(): Boolean
    fun getMissingPermissions(): List<RequiredPermission>
}