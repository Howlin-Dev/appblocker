package com.serhii.appblocker.timer.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.timerDataStore by preferencesDataStore(name = "timer_prefs")

class TimerDataStore(
    private val context: Context
) {
    private val END_TIME_KEY = longPreferencesKey("end_time")

    suspend fun setEndTime(timestamp: Long) {
        context.timerDataStore.edit {
            it[END_TIME_KEY] = timestamp
        }
    }

    fun observeEndTime(): Flow<Long> =
        context.timerDataStore.data.map { it[END_TIME_KEY] ?: 0L }

    suspend fun clear() {
        context.timerDataStore.edit {
            it.remove(END_TIME_KEY)
        }
    }
}
