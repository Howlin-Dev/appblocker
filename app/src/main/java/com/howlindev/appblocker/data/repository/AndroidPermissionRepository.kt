package com.howlindev.appblocker.data.repository

import android.app.AlarmManager
import android.app.AppOpsManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.PowerManager
import android.os.Process
import android.provider.Settings
import com.howlindev.appblocker.permissions.domain.model.RequiredPermission
import com.howlindev.appblocker.permissions.domain.repository.PermissionRepository
import com.howlindev.appblocker.permissions.platform.util.isMiui
import com.howlindev.appblocker.platform.accessibility.BlockAccessibilityService
import java.lang.reflect.Method

class AndroidPermissionRepository(
    private val context: Context,
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
        if (isMiui() && !hasMiuiBackgroundStartPermission()) missing.add(RequiredPermission.MiuiBackgroundStart)

        return missing
    }

    private fun hasMiuiBackgroundStartPermission(): Boolean {
        val ops = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        return try {
            val method: Method = ops.javaClass.getMethod(
                "checkOp",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                String::class.java,
            )
            val result = method.invoke(
                ops,
                10021, // OP_BACKGROUND_START_ACTIVITY
                Process.myUid(),
                context.packageName,
            ) as Int
            result == AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            true
        }
    }

    private fun isAccessibilityEnabled(): Boolean {
        val expectedService = ComponentName(context, BlockAccessibilityService::class.java)
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
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
            context.packageName,
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

