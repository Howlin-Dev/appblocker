package com.appblocker.permissions.domain.usecase

import com.appblocker.permissions.domain.model.RequiredPermission
import com.appblocker.permissions.platform.requester.PermissionRequester

class RequestPermissionUseCase(
    private val permissionRequester: PermissionRequester
) {
    operator fun invoke(permission: RequiredPermission) {
        permissionRequester.request(permission)
    }
}
