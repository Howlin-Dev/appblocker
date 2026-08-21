package com.howlindev.appblocker.permissions.platform.util

import android.annotation.SuppressLint
import android.os.Build

@SuppressLint("PrivateApi")
fun isOnePlus(): Boolean {
    val manufacturer = Build.MANUFACTURER
    val brand = Build.BRAND
    val isOnePlusBrand = manufacturer.equals("OnePlus", ignoreCase = true) ||
            brand.equals("OnePlus", ignoreCase = true) ||
            manufacturer.equals("OPPO", ignoreCase = true) ||
            brand.equals("OPPO", ignoreCase = true)

    if (isOnePlusBrand) return true

    // Check system properties for OxygenOS/ColorOS identification
    return try {
        val cls = Class.forName("android.os.SystemProperties")
        val get = cls.getMethod("get", String::class.java)
        val displayId = get.invoke(null, "ro.build.display.id") as String
        val otaVersion = get.invoke(null, "ro.build.ota.versionname") as String
        
        displayId.contains("Oxygen", ignoreCase = true) || 
        displayId.contains("ColorOS", ignoreCase = true) ||
        otaVersion.contains("Oxygen", ignoreCase = true)
    } catch (e: Exception) {
        false
    }
}
