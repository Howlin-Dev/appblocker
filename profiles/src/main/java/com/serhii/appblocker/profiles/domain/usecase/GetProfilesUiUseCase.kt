package com.serhii.appblocker.profiles.domain.usecase

import com.serhii.appblocker.profiles.domain.repository.InstalledAppsRepository
import com.serhii.appblocker.profiles.domain.repository.ProfilesRepository
import com.serhii.appblocker.profiles.presentation.list.model.ProfileUi
import com.serhii.appblocker.profiles.presentation.list.model.toUi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetProfilesUiUseCase(
    private val profilesRepository: ProfilesRepository,
    private val installedAppsRepository: InstalledAppsRepository
) {
    operator fun invoke(): Flow<List<ProfileUi>> = profilesRepository.getAll().map { profiles ->
        coroutineScope {
            profiles.map { profile ->
                async { profile.toUi(installedAppsRepository) }
            }.awaitAll()
        }
    }
}
