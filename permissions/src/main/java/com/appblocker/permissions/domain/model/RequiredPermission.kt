package com.appblocker.permissions.domain.model

sealed class RequiredPermission(val title: String, val subtitle: String) {
    object Accessibility : RequiredPermission("Accessibility Service (Control app usage)","Allow access to monitor and block selected apps in real time.")
    object Overlay : RequiredPermission("Overlay Permission (Show blocking screen)","Enable displaying a blocking screen when restricted apps are opened.")
    object UsageAccess : RequiredPermission("Usage Access (Track app activity)","Grant permission to detect which apps are currently in use.")
    object BatteryOptimization : RequiredPermission("Battery Optimization Exemption","Allow the app to stay active and enforce blocking consistently.")
    object ExactAlarm : RequiredPermission("Schedule Precise Blocking","Allow exact alarms to enforce app blocking at specific times without delay.")
}