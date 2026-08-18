package com.howlindev.appblocker.permissions.platform.util

import android.os.Build

fun isOnePlus(): Boolean {
    val manufacturer = Build.MANUFACTURER
    val brand = Build.BRAND
    return manufacturer.equals("OnePlus", ignoreCase = true) || 
           brand.equals("OnePlus", ignoreCase = true) ||
           manufacturer.equals("OPPO", ignoreCase = true) || // OnePlus uses ColorOS base
           brand.equals("OPPO", ignoreCase = true)
}
