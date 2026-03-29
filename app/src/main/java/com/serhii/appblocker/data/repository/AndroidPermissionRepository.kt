package com.serhii.appblocker.data.repository

import android.app.AlarmManager
import android.app.AppOpsManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.PowerManager
import android.os.Process
import android.provider.Settings
import com.serhii.appblocker.domain.model.RequiredPermission
import com.serhii.appblocker.domain.repository.PermissionRepository
import com.serhii.appblocker.platform.accessibility.BlockAccessibilityService

class AndroidPermissionRepository(
    private val context: Context
) : PermissionRepository {

    override fun hasRequiredPermissions(): Boolean {
        return getMissingPermissions().isEmpty()
    }

    override fun getMissingPermissions(): List<RequiredPermission> {
        val missing = mutableListOf<RequiredPermission>()

        if (!isAccessibilityEnabled()) missing.add(RequiredPermission.Accessibility)
        if (!hasOverlayPermission()) missing.add(RequiredPermission.Overlay)
        if (!hasUsageAccess()) missing.add(RequiredPermission.UsageAccess)
        if (!isIgnoringBatteryOptimizations()) missing.add(RequiredPermission.BatteryOptimization)

        return missing
    }

    private fun isAccessibilityEnabled(): Boolean {
        val expectedService = ComponentName(context, BlockAccessibilityService::class.java)
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return enabledServices.contains(expectedService.flattenToString())
    }
    private fun hasOverlayPermission(): Boolean {
        return Settings.canDrawOverlays(context)
     }
    private fun hasUsageAccess(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
     }
    private fun isIgnoringBatteryOptimizations(): Boolean {
        val packageName = context.packageName
        val pm = context.getSystemService(PowerManager::class.java)
        return pm.isIgnoringBatteryOptimizations(packageName)
     }

    private fun canScheduleExactAlarms(): Boolean {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            false
        }
    }
}