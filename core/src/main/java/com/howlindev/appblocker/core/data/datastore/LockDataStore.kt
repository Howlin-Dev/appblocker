package com.howlindev.appblocker.core.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.blockDataStore: DataStore<Preferences> by preferencesDataStore(name = "block_prefs")

object BlockPreferencesKeys {
    val ACTIVE_PROFILE_ID = longPreferencesKey("active_profile_id")
    val LOCKED_PACKAGES = stringSetPreferencesKey("locked_packages")
    val IS_TIMED = booleanPreferencesKey("is_timed")
}

