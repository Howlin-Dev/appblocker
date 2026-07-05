package com.howlindev.appblocker.profiles.domain.usecase

import com.howlindev.appblocker.core.domain.repository.SuggestedWebsitesRepository

class RemoveSuggestedWebsiteUseCase(
    private val repository: SuggestedWebsitesRepository,
) {
    suspend operator fun invoke(website: String) {
        repository.removeWebsite(website)
    }
}
