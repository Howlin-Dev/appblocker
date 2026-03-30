package com.serhii.appblocker.core.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.serhii.appblocker.core.data.datastore.BlockPreferencesKeys
import com.serhii.appblocker.core.data.datastore.blockDataStore
import com.serhii.appblocker.core.domain.model.ActiveBlock
import com.serhii.appblocker.core.domain.repository.BlockRepository
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
            blockedPackages = prefs[BlockPreferencesKeys.LOCKED_PACKAGES]?.toList() ?: emptyList()
        )
    }

    override suspend fun activateProfile(profileId: Long, blockedPackages: List<String>) {
        dataStore.edit { prefs ->
            prefs[BlockPreferencesKeys.ACTIVE_PROFILE_ID] = profileId
            prefs[BlockPreferencesKeys.LOCKED_PACKAGES] = blockedPackages.toSet()
        }
    }

    override suspend fun deactivate() {
        dataStore.edit { prefs ->
            prefs[BlockPreferencesKeys.ACTIVE_PROFILE_ID] = 0L
            prefs[BlockPreferencesKeys.LOCKED_PACKAGES] = emptySet()
        }
    }
}