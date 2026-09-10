package com.howlindev.appblocker.permissions.platform

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.content.edit
import androidx.core.net.toUri

class PermissionNavigator(private val context: Context) {

    fun openAccessibilitySettings() {
        setExpectingReturn()
        context.startActivity(
            Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
        )
    }

    fun openOverlaySettings() {
        setExpectingReturn()
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            "package:${context.packageName}".toUri(),
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun openUsageAccessSettings() {
        setExpectingReturn()
        context.startActivity(
            Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
        )
    }

    fun openNotificationListenerSettings() {
        setExpectingReturn()
        context.startActivity(
            Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
        )
    }

    fun requestBatteryOptimizationExemption() {
        setExpectingReturn()
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        intent.data = "package:${context.packageName}".toUri()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun openBackgroundPopupsSettings() {
        setExpectingReturn()
        val intents = listOf(
            Intent("miui.intent.action.APP_PERM_EDITOR").apply {
                setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity")
                putExtra("extra_pkgname", context.packageName)
            },
            Intent().apply {
                setClassName("com.coloros.safecenter", "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity")
            },
            Intent().apply {
                setClassName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.StartupAppListActivity",
                )
            },
            Intent("com.oppo.safe.permission.startup.StartupAppListActivity"),
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = "package:${context.packageName}".toUri()
            },
        )

        for (intent in intents) {
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return
            } catch (e: Exception) {
                // Continue to next intent
            }
        }
    }

    fun openAutostartSettings() {
        setExpectingReturn()
        try {
            val intent = Intent()
            intent.setClassName(
                "com.miui.securitycenter",
                "com.miui.permcenter.autostart.AutoStartManagementActivity",
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            openAppDetailsSettings()
        }
    }

    fun openXiaomiBatterySaverSettings() {
        setExpectingReturn()
        try {
            val intent = Intent()
            intent.setClassName(
                "com.miui.powerkeeper",
                "com.miui.powerkeeper.ui.HiddenAppsConfigActivity",
            )
            intent.putExtra("package_name", context.packageName)
            intent.putExtra("package_label", context.getString(context.applicationInfo.labelRes))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            openAppDetailsSettings()
        }
    }

    fun openAppNotificationSettings() {
        setExpectingReturn()
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                data = "package:${context.packageName}".toUri()
            }
        }
        context.startActivity(intent)
    }

    fun openAppDetails() {
        setExpectingReturn()
        openAppDetailsSettings()
    }

    private fun openAppDetailsSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = "package:${context.packageName}".toUri()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    private fun setExpectingReturn() {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit {
                putLong(KEY_LAST_OPEN_TIME, System.currentTimeMillis())
            }
    }

    companion object {
        private const val PREFS_NAME = "permission_navigator_prefs"
        private const val KEY_LAST_OPEN_TIME = "last_settings_open_time"
        private const val MAX_RETURN_DELAY_MS = 10 * 60 * 1000L

        fun shouldAutoReturn(context: Context): Boolean {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val lastOpenTime = prefs.getLong(KEY_LAST_OPEN_TIME, 0L)
            val now = System.currentTimeMillis()
            val shouldReturn = (now - lastOpenTime) < MAX_RETURN_DELAY_MS

            if (shouldReturn) {
                prefs.edit { remove(KEY_LAST_OPEN_TIME) }
            }

            return shouldReturn
        }
    }
}
