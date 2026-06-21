package com.howlindev.appblocker.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface SuggestedWebsitesRepository {
    fun getSuggestedWebsites(): Flow<Set<String>>
    suspend fun addWebsite(website: String)
    suspend fun removeWebsite(website: String)
}
