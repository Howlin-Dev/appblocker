package com.howlindev.appblocker.core.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.howlindev.appblocker.core.data.datastore.BlockPreferencesKeys
import com.howlindev.appblocker.core.data.datastore.blockDataStore
import com.howlindev.appblocker.core.domain.model.ActiveBlock
import com.howlindev.appblocker.core.domain.repository.BlockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BlockRepositoryImpl(
    context: Context,
) : BlockRepository {

    private val dataStore = context.blockDataStore

    override val activeBlock: Flow<ActiveBlock?> = dataStore.data.map { prefs ->
        val isTimedActive = prefs[BlockPreferencesKeys.IS_TIMED_ACTIVE] ?: false
        val isScheduledActive = prefs[BlockPreferencesKeys.IS_SCHEDULED_ACTIVE] ?: false
        val hasTimer = prefs[BlockPreferencesKeys.HAS_TIMER] ?: isTimedActive

        if (!isTimedActive && !isScheduledActive) return@map null

        val timedPackages = prefs[BlockPreferencesKeys.TIMED_PACKAGES] ?: emptySet()
        val scheduledPackages = prefs[BlockPreferencesKeys.SCHEDULED_PACKAGES] ?: emptySet()
        val mergedPackages = (timedPackages + scheduledPackages).toList()

        val timedWebsites = prefs[BlockPreferencesKeys.TIMED_WEBSITES] ?: emptySet()
        val scheduledWebsites = prefs[BlockPreferencesKeys.SCHEDULED_WEBSITES] ?: emptySet()
        val mergedWebsites = (timedWebsites + scheduledWebsites).toList()

        val scheduledEndTimesRaw = prefs[BlockPreferencesKeys.SCHEDULED_PROFILE_END_TIMES] ?: emptySet()
        val scheduledEndTimes = scheduledEndTimesRaw.mapNotNull { entry ->
            val parts = entry.split("|")
            if (parts.size == 2) {
                parts[0].toLongOrNull()?.let { id -> id to parts[1] }
            } else {
                null
            }
        }.toMap()

        ActiveBlock(
            profileId = if (isTimedActive) prefs[BlockPreferencesKeys.TIMED_PROFILE_ID] else null,
            blockedPackages = mergedPackages,
            blockedWebsites = mergedWebsites,
            isTimed = isTimedActive,
            isScheduled = isScheduledActive,
            hasTimer = hasTimer,
            scheduledProfileEndTimes = scheduledEndTimes,
        )
    }

    override suspend fun activateTimedProfile(
        profileId: Long,
        appPackages: List<String>,
        blockedWebsites: List<String>,
        hasTimer: Boolean,
    ) {
        dataStore.edit { prefs ->
            prefs[BlockPreferencesKeys.TIMED_PROFILE_ID] = profileId
            prefs[BlockPreferencesKeys.TIMED_PACKAGES] = appPackages.toSet()
            prefs[BlockPreferencesKeys.TIMED_WEBSITES] = blockedWebsites.toSet()
            prefs[BlockPreferencesKeys.IS_TIMED_ACTIVE] = true
            prefs[BlockPreferencesKeys.HAS_TIMER] = hasTimer
        }
    }

    override suspend fun deactivateTimed() {
        dataStore.edit { prefs ->
            prefs[BlockPreferencesKeys.IS_TIMED_ACTIVE] = false
            prefs[BlockPreferencesKeys.HAS_TIMER] = false
            prefs[BlockPreferencesKeys.TIMED_PROFILE_ID] = 0L
            prefs[BlockPreferencesKeys.TIMED_PACKAGES] = emptySet()
            prefs[BlockPreferencesKeys.TIMED_WEBSITES] = emptySet()
        }
    }

    override suspend fun activateScheduledBlocking(
        appPackages: List<String>,
        blockedWebsites: List<String>,
        profileEndTimes: Map<Long, String>,
    ) {
        dataStore.edit { prefs ->
            prefs[BlockPreferencesKeys.SCHEDULED_PACKAGES] = appPackages.toSet()
            prefs[BlockPreferencesKeys.SCHEDULED_WEBSITES] = blockedWebsites.toSet()
            prefs[BlockPreferencesKeys.IS_SCHEDULED_ACTIVE] = true
            prefs[BlockPreferencesKeys.SCHEDULED_PROFILE_END_TIMES] = profileEndTimes.map {
                "${it.key}|${it.value}"
            }.toSet()
        }
    }

    override suspend fun deactivateScheduled() {
        dataStore.edit { prefs ->
            prefs[BlockPreferencesKeys.IS_SCHEDULED_ACTIVE] = false
            prefs[BlockPreferencesKeys.SCHEDULED_PACKAGES] = emptySet()
            prefs[BlockPreferencesKeys.SCHEDULED_WEBSITES] = emptySet()
            prefs[BlockPreferencesKeys.SCHEDULED_PROFILE_END_TIMES] = emptySet()
        }
    }

    @Deprecated("Use activateTimedProfile or activateScheduledBlocking")
    override suspend fun activateProfile(
        profileId: Long,
        appPackages: List<String>,
        blockedWebsites: List<String>,
        isTimed: Boolean,
        isScheduled: Boolean,
    ) {
        activateTimedProfile(profileId, appPackages, blockedWebsites, isTimed)

        if (isScheduled) {
            activateScheduledBlocking(appPackages, blockedWebsites, emptyMap())
        }
    }

    @Deprecated("Use deactivateTimed or deactivateScheduled")
    override suspend fun deactivate() {
        deactivateTimed()
        deactivateScheduled()
    }
}
