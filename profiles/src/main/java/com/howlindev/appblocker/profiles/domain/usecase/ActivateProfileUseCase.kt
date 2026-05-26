package com.howlindev.appblocker.profiles.domain.usecase

import com.howlindev.appblocker.core.domain.repository.BlockRepository
import com.howlindev.appblocker.core.domain.repository.TimerRepository
import com.howlindev.appblocker.profiles.domain.model.Profile

class ActivateProfileUseCase(
    private val blockRepository: BlockRepository,
    private val timerRepository: TimerRepository,
) {
    suspend operator fun invoke(profile: Profile) {
        profile.durationMillis?.let {
            timerRepository.startTimer(it)
        }

        blockRepository.activateProfile(
            profileId = profile.id,
            appPackages = profile.appPackages,
            isTimed = profile.durationMillis != null,
        )
    }
}

