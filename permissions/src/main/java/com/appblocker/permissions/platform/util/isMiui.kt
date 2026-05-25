package com.appblocker.permissions.platform.util

import android.annotation.SuppressLint
import android.os.Build

// TODO

@SuppressLint("PrivateApi")
fun isMiui(): Boolean {
    val manufacturer = Build.MANUFACTURER
    if (!manufacturer.equals("Xiaomi", ignoreCase = true)) return false
    val prop = try {
        val cls = Class.forName("android.os.SystemProperties")
        val get = cls.getMethod("get", String::class.java)
        get.invoke(cls, "ro.miui.ui.version.name") as String
    } catch (e: Exception) { "" }
    return prop.isNotEmpty()
}
