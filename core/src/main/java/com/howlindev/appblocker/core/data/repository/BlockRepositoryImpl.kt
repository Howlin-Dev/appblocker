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
        val profileId = prefs[BlockPreferencesKeys.ACTIVE_PROFILE_ID] ?: 0L
        if (profileId == 0L) return@map null

        ActiveBlock(
            profileId = profileId,
            blockedPackages = prefs[BlockPreferencesKeys.LOCKED_PACKAGES]?.toList() ?: emptyList(),
            isTimed = prefs[BlockPreferencesKeys.IS_TIMED] ?: false,
        )
    }

    override suspend fun activateProfile(profileId: Long, appPackages: List<String>, isTimed: Boolean) {
        dataStore.edit { prefs ->
            prefs[BlockPreferencesKeys.ACTIVE_PROFILE_ID] = profileId
            prefs[BlockPreferencesKeys.LOCKED_PACKAGES] = appPackages.toSet()
            prefs[BlockPreferencesKeys.IS_TIMED] = isTimed
        }
    }

    override suspend fun deactivate() {
        dataStore.edit { prefs ->
            prefs[BlockPreferencesKeys.ACTIVE_PROFILE_ID] = 0L
            prefs[BlockPreferencesKeys.LOCKED_PACKAGES] = emptySet()
        }
    }
}
