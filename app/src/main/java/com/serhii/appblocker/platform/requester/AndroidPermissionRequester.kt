package com.serhii.appblocker.platform.requester

import com.serhii.appblocker.domain.model.RequiredPermission
import com.serhii.appblocker.platform.PermissionNavigator

class PermissionRequester(
    private val navigator: PermissionNavigator
)  {

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