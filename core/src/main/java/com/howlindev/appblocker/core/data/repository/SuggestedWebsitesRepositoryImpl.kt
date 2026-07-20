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
        "youtube.com", "tiktok.com", "instagram.com", "facebook.com", "x.com", "twitter.com", "reddit.com",
        "twitch.tv", "discord.com", "snapchat.com", "linkedin.com", "pinterest.com", "netflix.com", "primevideo.com",
        "disneyplus.com", "hulu.com", "spotify.com", "soundcloud.com", "medium.com", "quora.com", "9gag.com", "imgur.com",
        "buzzfeed.com", "news.yahoo.com", "cnn.com", "nytimes.com", "espn.com", "chess.com", "roblox.com", "fandom.com",
        "wikia.com", "steamcommunity.com", "epicgames.com", "minecraft.net", "stackoverflow.com", "news.ycombinator.com",
        "tumblr.com", "telegram.org", "web.telegram.org", "whatsapp.com", "web.whatsapp.com", "messenger.com",
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
