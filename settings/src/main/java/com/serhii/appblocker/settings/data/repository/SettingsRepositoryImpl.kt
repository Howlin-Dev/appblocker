package com.serhii.appblocker.settings.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.serhii.appblocker.core.domain.model.AppLanguage
import com.serhii.appblocker.core.domain.model.SettingsData
import com.serhii.appblocker.core.domain.model.ThemeMode
import com.serhii.appblocker.settings.data.datastore.SettingsKeys
import com.serhii.appblocker.settings.data.datastore.settingsDataStore
import com.serhii.appblocker.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(
    private val context: Context,
) : SettingsRepository {

    override val settings: Flow<SettingsData> =
        context.settingsDataStore.data.map { preferences ->

            val themeMode = preferences[SettingsKeys.THEME_MODE]
                ?.let {
                    runCatching { ThemeMode.valueOf(it) }.getOrNull()
                }
                ?: ThemeMode.SYSTEM

            val language = preferences[SettingsKeys.LANGUAGE]
                ?.let {
                    runCatching { AppLanguage.valueOf(it) }.getOrNull()
                }
                ?: AppLanguage.SYSTEM

            val dynamicColor = preferences[SettingsKeys.DYNAMIC_COLOR] ?: true

            SettingsData(
                themeMode = themeMode,
                language = language,
                dynamicColor = dynamicColor,
            )
        }

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        context.settingsDataStore.edit { preferences ->
            preferences[SettingsKeys.THEME_MODE] = themeMode.name
        }
    }

    override suspend fun setLanguage(language: AppLanguage) {
        context.settingsDataStore.edit { preferences ->
            preferences[SettingsKeys.LANGUAGE] = language.name
        }
    }

    override suspend fun setDynamicColor(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[SettingsKeys.DYNAMIC_COLOR] = enabled
        }
    }
}
