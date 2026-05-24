package com.serhii.appblocker.settings.platform

import android.content.Context

fun Context.appVersion(): String {
    return packageManager
        .getPackageInfo(packageName, 0)
        .versionName ?: "Unknown"
}