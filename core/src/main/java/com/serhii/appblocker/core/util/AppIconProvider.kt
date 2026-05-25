package com.serhii.appblocker.core.util

import android.graphics.drawable.Drawable

interface AppIconProvider {
    fun getIcon(packageName: String): Drawable?
}
