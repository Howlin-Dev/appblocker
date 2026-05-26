package com.howlindev.appblocker.settings.presentation.settings

import com.howlindev.appblocker.core.domain.model.ThemeMode

sealed interface SettingsAction {
    data object BackClick : SettingsAction
    data object LanguageClick : SettingsAction
    data class ThemeModeSwitch(val themeMode: ThemeMode) : SettingsAction
    data class DynamicColorSwitch(val enabled: Boolean) : SettingsAction
}

