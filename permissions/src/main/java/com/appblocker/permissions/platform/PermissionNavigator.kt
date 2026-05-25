package com.appblocker.permissions.platform

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.net.toUri

class PermissionNavigator(private val context: Context) {

    fun openAccessibilitySettings() {
        context.startActivity(
            Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
        )
    }

    fun openOverlaySettings() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            "package:${context.packageName}".toUri(),
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun openUsageAccessSettings() {
        context.startActivity(
            Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
        )
    }

    fun requestBatteryOptimizationExemption() {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        intent.data = "package:${context.packageName}".toUri()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
