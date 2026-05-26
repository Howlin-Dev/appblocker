package com.serhii.appblocker.core.domain.model

data class SettingsData(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val language: AppLanguage = AppLanguage.SYSTEM,
    val dynamicColor: Boolean = true,
)
