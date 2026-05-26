package com.howlindev.appblocker.profiles.domain.repository

import com.howlindev.appblocker.profiles.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfilesRepository {
    fun getAll(): Flow<List<Profile>>
    suspend fun getById(id: Long): Profile
    suspend fun insert(profile: Profile)
    suspend fun update(profile: Profile)
    suspend fun delete(profileId: Long)
}

