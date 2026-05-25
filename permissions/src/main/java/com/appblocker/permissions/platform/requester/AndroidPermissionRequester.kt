package com.appblocker.permissions.platform.requester

import com.appblocker.permissions.domain.model.RequiredPermission
import com.appblocker.permissions.platform.PermissionNavigator

class PermissionRequester(
    private val navigator: PermissionNavigator,
) {

    fun request(permission: RequiredPermission) {
        when (permission) {
            RequiredPermission.Accessibility -> navigator.openAccessibilitySettings()
            RequiredPermission.Overlay -> navigator.openOverlaySettings()
            RequiredPermission.UsageAccess -> navigator.openUsageAccessSettings()
            RequiredPermission.BatteryOptimization -> navigator.requestBatteryOptimizationExemption()
            else -> {}
        }
    }
}
