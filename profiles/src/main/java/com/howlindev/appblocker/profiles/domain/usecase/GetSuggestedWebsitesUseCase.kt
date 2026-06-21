package com.howlindev.appblocker.profiles.domain.usecase

import com.howlindev.appblocker.core.domain.repository.SuggestedWebsitesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetSuggestedWebsitesUseCase(
    private val repository: SuggestedWebsitesRepository
) {
    operator fun invoke(): Flow<List<String>> = repository.getSuggestedWebsites().map { it.toList().sorted() }
}
