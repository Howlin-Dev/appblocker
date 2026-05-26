package com.howlindev.appblocker.settings.domain.usecase

import com.howlindev.appblocker.core.domain.model.SettingsData
import com.howlindev.appblocker.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class GetSettingsUseCase(
    private val repository: SettingsRepository,
) {

    operator fun invoke(): Flow<SettingsData> {
        return repository.settings
    }
}

