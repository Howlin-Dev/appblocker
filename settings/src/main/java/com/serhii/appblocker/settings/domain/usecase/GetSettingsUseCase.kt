package com.serhii.appblocker.settings.domain.usecase

import com.serhii.appblocker.core.domain.model.SettingsData
import com.serhii.appblocker.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class GetSettingsUseCase(
    private val repository: SettingsRepository
) {

    operator fun invoke(): Flow<SettingsData> {
        return repository.settings
    }
}