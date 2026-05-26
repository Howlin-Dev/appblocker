package com.howlindev.appblocker.timer.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.timerDataStore by preferencesDataStore(name = "timer_prefs")

class TimerDataStore(
    private val context: Context,
) {
    private val endTimeKey = longPreferencesKey("end_time")

    suspend fun setEndTime(timestamp: Long) {
        context.timerDataStore.edit {
            it[endTimeKey] = timestamp
        }
    }

    fun observeEndTime(): Flow<Long> =
        context.timerDataStore.data.map { it[endTimeKey] ?: 0L }

    suspend fun clear() {
        context.timerDataStore.edit {
            it.remove(endTimeKey)
        }
    }
}

