package com.howlindev.appblocker.core.domain.repository

import com.howlindev.appblocker.core.domain.model.ActiveBlock
import kotlinx.coroutines.flow.Flow

interface BlockRepository {
    val activeBlock: Flow<ActiveBlock?>

    suspend fun activateTimedProfile(
        profileId: Long,
        appPackages: List<String>,
        blockedWebsites: List<String>,
        hasTimer: Boolean,
    )

    suspend fun deactivateTimed()

    suspend fun activateScheduledBlocking(
        appPackages: List<String>,
        blockedWebsites: List<String>,
        profileEndTimes: Map<Long, String>,
    )

    suspend fun deactivateScheduled()

    @Deprecated("Use activateTimedProfile or activateScheduledBlocking")
    suspend fun activateProfile(
        profileId: Long,
        appPackages: List<String>,
        blockedWebsites: List<String>,
        isTimed: Boolean,
        isScheduled: Boolean = false,
    )

    @Deprecated("Use deactivateTimed or deactivateScheduled")
    suspend fun deactivate()
}
