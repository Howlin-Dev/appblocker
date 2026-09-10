package com.howlindev.appblocker.data.repository

import android.Manifest
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.AlarmManager
import android.app.AppOpsManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.os.Build
import android.os.PowerManager
import android.os.Process
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityManager
import androidx.core.content.ContextCompat
import com.howlindev.appblocker.permissions.domain.model.RequiredPermission
import com.howlindev.appblocker.permissions.domain.repository.PermissionRepository
import com.howlindev.appblocker.core.util.DeviceUtils
import com.howlindev.appblocker.permissions.platform.util.isOnePlus
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

        val isRestricted = !isRestrictedSettingsEnabled()
        val onePlus = isOnePlus()
        val accessibilityEnabled = isAccessibilityServiceEnabledInSettings()
        val accessibilityRunning = isAccessibilityServiceRunning()

        if (!accessibilityEnabled) {
            missing.add(
                RequiredPermission.Accessibility(
                    isMalfunctioning = false,
                    restricted = isRestricted,
                    onePlus = onePlus,
                ),
            )
        } else if (!accessibilityRunning) {
            missing.add(
                RequiredPermission.Accessibility(
                    isMalfunctioning = true,
                    restricted = false,
                    onePlus = onePlus,
                ),
            )
        }

        if (!hasOverlayPermission()) {
            missing.add(RequiredPermission.Overlay(restricted = isRestricted, onePlus = onePlus))
        }
        if (!hasUsageAccess()) {
            missing.add(RequiredPermission.UsageAccess(restricted = isRestricted, onePlus = onePlus))
        }
        if (!isNotificationListenerEnabled()) {
            missing.add(RequiredPermission.NotificationListener(restricted = isRestricted, onePlus = onePlus))
        }
        if (!isIgnoringBatteryOptimizations()) {
            missing.add(RequiredPermission.BatteryOptimization(restricted = isRestricted, onePlus = onePlus))
        }
        if (!hasPostNotificationPermission()) missing.add(RequiredPermission.PostNotifications(onePlus = onePlus))

        if (DeviceUtils.isMiui() || isOnePlus()) {
            if (!hasBackgroundStartPermission()) {
                missing.add(RequiredPermission.BackgroundPopups(onePlus = onePlus))
            }
        }

        if (DeviceUtils.isMiui()) {
            if (!isAutostartEnabled()) missing.add(RequiredPermission.Autostart(onePlus = onePlus))
        }

        return missing
    }

    private fun isRestrictedSettingsEnabled(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager

        return try {
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                appOps.unsafeCheckOpNoThrow(
                    "android:access_restricted_settings",
                    Process.myUid(),
                    context.packageName,
                )
            } else {
                val method = appOps.javaClass.getMethod(
                    "checkOpNoThrow",
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    String::class.java,
                )
                method.invoke(
                    appOps,
                    119, // OP_ACCESS_RESTRICTED_SETTINGS
                    Process.myUid(),
                    context.packageName,
                ) as Int
            }
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            true
        }
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

    private fun hasBackgroundStartPermission(): Boolean {
        val ops = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val opCode = if (DeviceUtils.isMiui()) {
            10021 // OP_BACKGROUND_START_ACTIVITY
        } else if (isOnePlus()) {
            10021 // OP_BACKGROUND_START_ACTIVITY
        } else {
            return true
        }

        return try {
            val method: Method = ops.javaClass.getMethod(
                "checkOp",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                String::class.java,
            )
            val result = method.invoke(
                ops,
                opCode,
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
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager

        // Method 1: Check Settings.Secure
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
        )
        val isEnabledInSettings = enabledServices?.contains(expectedService.flattenToString()) == true

        // Method 2: Check AccessibilityManager (more reliable on some OEMs)
        val enabledServiceList = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        val isEnabledInManager = enabledServiceList.any {
            it.resolveInfo.serviceInfo.packageName == context.packageName &&
                it.resolveInfo.serviceInfo.name == BlockAccessibilityService::class.java.name
        }

        return isEnabledInSettings || isEnabledInManager
    }

    private fun isAccessibilityServiceRunning(): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
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
        if (!Settings.canDrawOverlays(context)) return false

        // Active verification for OnePlus/ColorOS false positives
        return try {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val view = View(context)
            val params = WindowManager.LayoutParams(
                1,
                1,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    @Suppress("DEPRECATION")
                    WindowManager.LayoutParams.TYPE_PHONE
                },
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSPARENT,
            )
            windowManager.addView(view, params)
            // If addView didn't throw, we likely have the permission.
            // Removing the token check as it's assigned asynchronously and causes false negatives.
            windowManager.removeView(view)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun hasUsageAccess(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName,
        )
        if (mode != AppOpsManager.MODE_ALLOWED) return false

        // Secondary check for false positives on some OEMs
        return try {
            val usageStatsManager = context.getSystemService(
                Context.USAGE_STATS_SERVICE,
            ) as android.app.usage.UsageStatsManager
            val endTime = System.currentTimeMillis()
            val startTime = endTime - (1000 * 60 * 60 * 24) // Check last 24 hours for more reliability

            val stats = usageStatsManager.queryUsageStats(
                android.app.usage.UsageStatsManager.INTERVAL_DAILY,
                startTime,
                endTime,
            )
            // On OxygenOS 16, it might return an empty list if denied instead of null
            !stats.isNullOrEmpty()
        } catch (e: Exception) {
            false
        }
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
        val componentName = ComponentName(
            context,
            com.howlindev.appblocker.platform.notification.BlockNotificationListenerService::class.java,
        )
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
