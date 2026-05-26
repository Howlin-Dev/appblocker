package com.howlindev.appblocker.permissions.domain.repository

import com.howlindev.appblocker.permissions.domain.model.RequiredPermission

interface PermissionRepository {
    fun hasRequiredPermissions(): Boolean
    fun getMissingPermissions(): List<RequiredPermission>
}

