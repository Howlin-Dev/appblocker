package com.serhii.appblocker.settings.presentation.settings

import com.serhii.appblocker.core.domain.model.ThemeMode

sealed interface SettingsAction {
    data object BackClick : SettingsAction
    data object LanguageClick : SettingsAction
    data class ThemeModeSwitch(val themeMode: ThemeMode) : SettingsAction
    data class DynamicColorSwitch(val enabled: Boolean) : SettingsAction
}
