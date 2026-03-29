package com.serhii.appblocker.core.domain.model

import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val name: String,
    val drawable: Drawable? = null,
)