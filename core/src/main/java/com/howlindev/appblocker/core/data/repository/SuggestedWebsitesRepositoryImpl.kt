package com.howlindev.appblocker.core.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.howlindev.appblocker.core.data.datastore.BlockPreferencesKeys
import com.howlindev.appblocker.core.data.datastore.blockDataStore
import com.howlindev.appblocker.core.domain.repository.SuggestedWebsitesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SuggestedWebsitesRepositoryImpl(
    context: Context,
) : SuggestedWebsitesRepository {

    private val dataStore = context.blockDataStore

    private val defaultWebsites = setOf(
        "facebook.com", "instagram.com", "twitter.com", "tiktok.com", "youtube.com",
        "reddit.com", "netflix.com", "twitch.tv", "pinterest.com", "snapchat.com",
        "linkedin.com", "whatsapp.com", "messenger.com", "telegram.org", "discord.com",
        "tumblr.com", "quora.com", "medium.com", "9gag.com", "buzzfeed.com",
        "hulu.com", "disneyplus.com", "primevideo.com", "hbomax.com", "spotify.com",
        "soundcloud.com", "steamcommunity.com", "epicgames.com", "roblox.com", "minecraft.net",
    )

    override fun getSuggestedWebsites(): Flow<Set<String>> = dataStore.data.map { prefs ->
        prefs[BlockPreferencesKeys.SUGGESTED_WEBSITES] ?: defaultWebsites
    }

    override suspend fun addWebsite(website: String) {
        dataStore.edit { prefs ->
            val current = prefs[BlockPreferencesKeys.SUGGESTED_WEBSITES] ?: defaultWebsites
            prefs[BlockPreferencesKeys.SUGGESTED_WEBSITES] = current + website
        }
    }

    override suspend fun removeWebsite(website: String) {
        dataStore.edit { prefs ->
            val current = prefs[BlockPreferencesKeys.SUGGESTED_WEBSITES] ?: defaultWebsites
            prefs[BlockPreferencesKeys.SUGGESTED_WEBSITES] = current - website
        }
    }
}
