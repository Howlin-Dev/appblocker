package com.howlindev.appblocker.permissions.domain.model

import androidx.annotation.StringRes
import com.howlindev.appblocker.permissions.R

sealed class RequiredPermission(
    @param:StringRes val titleRes: Int,
    @param:StringRes val subtitleRes: Int,
    val isRestricted: Boolean = false,
    val isOnePlus: Boolean = false,
) {
    data class Accessibility(
        val isMalfunctioning: Boolean = false,
        val restricted: Boolean = false,
        val onePlus: Boolean = false,
    ) : RequiredPermission(
        titleRes = R.string.permission_accessibility_title,
        subtitleRes = R.string.permission_accessibility_subtitle,
        isRestricted = restricted,
        isOnePlus = onePlus,
    )
    data class Overlay(
        val restricted: Boolean = false,
        val onePlus: Boolean = false,
    ) : RequiredPermission(
        titleRes = R.string.permission_overlay_title,
        subtitleRes = R.string.permission_overlay_subtitle,
        isRestricted = restricted,
        isOnePlus = onePlus,
    )
    data class UsageAccess(
        val restricted: Boolean = false,
        val onePlus: Boolean = false,
    ) : RequiredPermission(
        titleRes = R.string.permission_usage_title,
        subtitleRes = R.string.permission_usage_subtitle,
        isRestricted = restricted,
        isOnePlus = onePlus,
    )
    data class BatteryOptimization(
        val restricted: Boolean = false,
        val onePlus: Boolean = false,
    ) : RequiredPermission(
        titleRes = R.string.permission_battery_title,
        subtitleRes = R.string.permission_battery_subtitle,
        isRestricted = restricted,
        isOnePlus = onePlus,
    )
    data class ExactAlarm(val onePlus: Boolean = false) : RequiredPermission(
        titleRes = R.string.permission_alarm_title,
        subtitleRes = R.string.permission_alarm_subtitle,
        isOnePlus = onePlus,
    )
    data class NotificationListener(
        val restricted: Boolean = false,
        val onePlus: Boolean = false,
    ) : RequiredPermission(
        titleRes = R.string.permission_notification_listener_title,
        subtitleRes = R.string.permission_notification_listener_subtitle,
        isRestricted = restricted,
        isOnePlus = onePlus,
    )
    data class MiuiBackgroundStart(val onePlus: Boolean = false) : RequiredPermission(
        titleRes = R.string.permission_miui_background_title,
        subtitleRes = R.string.permission_miui_background_subtitle,
        isOnePlus = onePlus,
    )
    data class PostNotifications(val onePlus: Boolean = false) : RequiredPermission(
        titleRes = R.string.permission_post_notifications_title,
        subtitleRes = R.string.permission_post_notifications_subtitle,
        isOnePlus = onePlus,
    )
    data class Autostart(val onePlus: Boolean = false) : RequiredPermission(
        titleRes = R.string.permission_autostart_title,
        subtitleRes = R.string.permission_autostart_subtitle,
        isOnePlus = onePlus,
    )
}
