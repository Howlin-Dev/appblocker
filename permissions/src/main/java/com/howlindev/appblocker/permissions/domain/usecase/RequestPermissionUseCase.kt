package com.howlindev.appblocker.permissions.domain.usecase

import com.howlindev.appblocker.permissions.domain.model.RequiredPermission
import com.howlindev.appblocker.permissions.platform.requester.PermissionRequester

class RequestPermissionUseCase(
    private val permissionRequester: PermissionRequester,
) {
    operator fun invoke(permission: RequiredPermission) {
        permissionRequester.request(permission)
    }
}

