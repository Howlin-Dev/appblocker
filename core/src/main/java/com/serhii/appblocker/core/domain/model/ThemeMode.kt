package com.serhii.appblocker.core.domain.model

import androidx.annotation.StringRes
import com.serhii.appblocker.core.R

enum class ThemeMode(
    @StringRes val titleRes: Int
) {

    LIGHT(R.string.theme_light),
    DARK(R.string.theme_dark),
    SYSTEM(R.string.theme_system)
}