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
    val TIMED_PROFILE_ID = longPreferencesKey("timed_profile_id")
    val TIMED_PACKAGES = stringSetPreferencesKey("timed_packages")
    val TIMED_WEBSITES = stringSetPreferencesKey("timed_websites")
    val IS_TIMED_ACTIVE = booleanPreferencesKey("is_timed_active")
    val HAS_TIMER = booleanPreferencesKey("has_timer")

    val SCHEDULED_PACKAGES = stringSetPreferencesKey("scheduled_packages")
    val SCHEDULED_WEBSITES = stringSetPreferencesKey("scheduled_websites")
    val IS_SCHEDULED_ACTIVE = booleanPreferencesKey("is_scheduled_active")
    val SCHEDULED_PROFILE_END_TIMES = stringSetPreferencesKey("scheduled_profile_end_times")

    val SUGGESTED_WEBSITES = stringSetPreferencesKey("suggested_websites")

    // Legacy keys
    val ACTIVE_PROFILE_ID = longPreferencesKey("active_profile_id")
    val LOCKED_PACKAGES = stringSetPreferencesKey("locked_packages")
    val BLOCKED_WEBSITES = stringSetPreferencesKey("blocked_websites")
    val IS_TIMED = booleanPreferencesKey("is_timed")
    val IS_SCHEDULED = booleanPreferencesKey("is_scheduled")
}
