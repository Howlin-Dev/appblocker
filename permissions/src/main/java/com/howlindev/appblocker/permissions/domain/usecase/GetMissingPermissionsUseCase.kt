package com.howlindev.appblocker.permissions.domain.usecase

import com.howlindev.appblocker.permissions.domain.model.RequiredPermission
import com.howlindev.appblocker.permissions.domain.repository.PermissionRepository

class GetMissingPermissionsUseCase(
    private val repository: PermissionRepository,
) {
    operator fun invoke(): List<RequiredPermission> = repository.getMissingPermissions()
}

