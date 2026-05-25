package com.appblocker.permissions.domain.usecase

import com.appblocker.permissions.domain.model.RequiredPermission
import com.appblocker.permissions.domain.repository.PermissionRepository

class GetMissingPermissionsUseCase(
    private val repository: PermissionRepository,
) {
    operator fun invoke(): List<RequiredPermission> = repository.getMissingPermissions()
}
