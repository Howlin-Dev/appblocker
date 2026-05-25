package com.serhii.appblocker.settings.domain.repository

import com.serhii.appblocker.core.domain.model.AppLanguage
import com.serhii.appblocker.core.domain.model.SettingsData
import com.serhii.appblocker.core.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    val settings: Flow<SettingsData>

    suspend fun setThemeMode(themeMode: ThemeMode)

    suspend fun setLanguage(language: AppLanguage)
}
