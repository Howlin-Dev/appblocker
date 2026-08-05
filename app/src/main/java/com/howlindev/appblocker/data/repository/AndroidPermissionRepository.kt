package com.howlindev.appblocker.data.repository

import android.Manifest
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.AlarmManager
import android.app.AppOpsManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import android.os.Process
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.core.content.ContextCompat
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

        val accessibilityEnabled = isAccessibilityServiceEnabledInSettings()
        val accessibilityRunning = isAccessibilityServiceRunning()

        if (!accessibilityEnabled) {
            missing.add(RequiredPermission.Accessibility(isMalfunctioning = false))
        } else if (!accessibilityRunning) {
            missing.add(RequiredPermission.Accessibility(isMalfunctioning = true))
        }

        if (!hasOverlayPermission()) missing.add(RequiredPermission.Overlay)
        if (!hasUsageAccess()) missing.add(RequiredPermission.UsageAccess)
        if (!isNotificationListenerEnabled()) missing.add(RequiredPermission.NotificationListener)
        if (!isIgnoringBatteryOptimizations()) missing.add(RequiredPermission.BatteryOptimization)
        if (!hasPostNotificationPermission()) missing.add(RequiredPermission.PostNotifications)
        if (isMiui()) {
            if (!hasMiuiBackgroundStartPermission()) missing.add(RequiredPermission.MiuiBackgroundStart)
            if (!isAutostartEnabled()) missing.add(RequiredPermission.Autostart)
        }

        return missing
    }

    private fun hasPostNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
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

    private fun isAccessibilityServiceEnabledInSettings(): Boolean {
        val expectedService = ComponentName(context, BlockAccessibilityService::class.java)
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
        ) ?: return false
        return enabledServices.contains(expectedService.flattenToString())
    }

    private fun isAccessibilityServiceRunning(): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
        return enabledServices.any {
            it.resolveInfo.serviceInfo.packageName == context.packageName &&
                it.resolveInfo.serviceInfo.name == BlockAccessibilityService::class.java.name
        }
    }

    private fun isAutostartEnabled(): Boolean {
        val ops = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        return try {
            val method: Method = ops.javaClass.getMethod(
                "checkOpNoThrow",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                String::class.java,
            )
            val result = method.invoke(
                ops,
                10008, // OP_AUTO_START (MIUI specific)
                Process.myUid(),
                context.packageName,
            ) as Int
            result == AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            true
        }
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

    private fun isNotificationListenerEnabled(): Boolean {
        val enabledListeners = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners",
        )
        val componentName =
            ComponentName(context, "com.howlindev.appblocker.platform.notification.BlockNotificationListenerService")
        return enabledListeners?.contains(componentName.flattenToString()) == true
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
