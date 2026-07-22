package com.howlindev.appblocker.profiles.domain.usecase

import com.howlindev.appblocker.core.domain.model.Profile
import com.howlindev.appblocker.core.domain.repository.BlockRepository
import com.howlindev.appblocker.core.domain.repository.TimerRepository

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
            blockedWebsites = profile.blockedWebsites,
            isTimed = profile.durationMillis != null,
        )
    }
}
