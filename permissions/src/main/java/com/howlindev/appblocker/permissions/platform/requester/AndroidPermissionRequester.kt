package com.howlindev.appblocker.permissions.platform.requester

import com.howlindev.appblocker.permissions.domain.model.RequiredPermission
import com.howlindev.appblocker.permissions.platform.PermissionNavigator

class PermissionRequester(
    private val navigator: PermissionNavigator,
) {

    fun request(permission: RequiredPermission) {
        if (permission.isRestricted) {
            navigator.openAppDetails()
            return
        }
        when (permission) {
            is RequiredPermission.Accessibility -> navigator.openAccessibilitySettings()
            is RequiredPermission.Overlay -> navigator.openOverlaySettings()
            is RequiredPermission.UsageAccess -> navigator.openUsageAccessSettings()
            is RequiredPermission.NotificationListener -> navigator.openNotificationListenerSettings()
            is RequiredPermission.BatteryOptimization -> navigator.requestBatteryOptimizationExemption()
            RequiredPermission.MiuiBackgroundStart -> navigator.openMiuiPermissionSettings()
            RequiredPermission.PostNotifications -> navigator.openAppNotificationSettings()
            RequiredPermission.Autostart -> navigator.openAutostartSettings()
            else -> {}
        }
    }
}
