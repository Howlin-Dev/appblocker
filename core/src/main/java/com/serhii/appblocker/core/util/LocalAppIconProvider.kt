package com.serhii.appblocker.core.util

import androidx.compose.runtime.staticCompositionLocalOf

val LocalAppIconProvider = staticCompositionLocalOf<AppIconProvider> {
    error("No AppIconProvider provided")
}