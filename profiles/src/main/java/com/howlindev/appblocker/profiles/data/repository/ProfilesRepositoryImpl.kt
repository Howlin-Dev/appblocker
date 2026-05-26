package com.howlindev.appblocker.profiles.data.repository

import com.howlindev.appblocker.profiles.data.db.ProfileDao
import com.howlindev.appblocker.profiles.data.model.toDomain
import com.howlindev.appblocker.profiles.data.model.toEntity
import com.howlindev.appblocker.profiles.domain.model.Profile
import com.howlindev.appblocker.profiles.domain.repository.ProfilesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfilesRepositoryImpl(
    private val profileDao: ProfileDao,
) : ProfilesRepository {

    override fun getAll(): Flow<List<Profile>> {
        return profileDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getById(id: Long): Profile {
        return profileDao.getById(id)?.toDomain() ?: throw NoSuchElementException("Profile with id $id not found")
    }

    override suspend fun insert(profile: Profile) {
        profileDao.insert(profile.toEntity())
    }

    override suspend fun update(profile: Profile) {
        profileDao.update(profile.toEntity())
    }

    override suspend fun delete(profileId: Long) {
        profileDao.deleteById(profileId)
    }
}

