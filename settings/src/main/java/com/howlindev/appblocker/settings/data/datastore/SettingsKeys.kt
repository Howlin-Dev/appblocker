package com.howlindev.appblocker.settings.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object SettingsKeys {

    val THEME_MODE = stringPreferencesKey("theme_mode")

    val LANGUAGE = stringPreferencesKey("language")

    val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
}

