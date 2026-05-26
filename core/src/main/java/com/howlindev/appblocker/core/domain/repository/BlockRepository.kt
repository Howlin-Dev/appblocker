package com.howlindev.appblocker.core.domain.repository

import com.howlindev.appblocker.core.domain.model.ActiveBlock
import kotlinx.coroutines.flow.Flow

interface BlockRepository {
    val activeBlock: Flow<ActiveBlock?>
    suspend fun activateProfile(profileId: Long, appPackages: List<String>, isTimed: Boolean)
    suspend fun deactivate()
}

