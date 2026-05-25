package com.serhii.appblocker.profiles.data.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.serhii.appblocker.profiles.data.model.ProfileEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

// We need a database to get the DAO. Since the AppDatabase is in the app module,
// we might need to create a test-specific database here or use the one from app if visible.
// Given it's a multi-module project, profiles shouldn't depend on app.
// I'll create a minimal TestDatabase inside the profiles module for testing the DAO.

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ProfileEntity::class], version = 1, exportSchema = false)
abstract class TestDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
}

@RunWith(AndroidJUnit4::class)
class ProfileDaoTest {

    private lateinit var profileDao: ProfileDao
    private lateinit var db: TestDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, TestDatabase::class.java
        ).build()
        profileDao = db.profileDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeProfileAndReadInList() = runTest {
        val profile = ProfileEntity(
            id = 1,
            name = "Work",
            description = "Work profile",
            blockedAppsPackageNames = "com.android.settings,com.google.android.youtube",
            durationMillis = 3600000L
        )
        profileDao.insert(profile)
        val allProfiles = profileDao.getAll().first()
        assertEquals(allProfiles[0], profile)
    }

    @Test
    @Throws(Exception::class)
    fun updateProfileAndRead() = runTest {
        val profile = ProfileEntity(
            id = 1,
            name = "Work",
            description = "Work profile",
            blockedAppsPackageNames = "com.android.settings",
            durationMillis = 3600000L
        )
        profileDao.insert(profile)

        val updatedProfile = profile.copy(name = "Working")
        profileDao.update(updatedProfile)

        val result = profileDao.getById(1)
        assertEquals("Working", result?.name)
    }

    @Test
    @Throws(Exception::class)
    fun deleteProfileAndCheck() = runTest {
        val profile = ProfileEntity(
            id = 1,
            name = "Work",
            description = "Work profile",
            blockedAppsPackageNames = "com.android.settings",
            durationMillis = 3600000L
        )
        profileDao.insert(profile)
        profileDao.deleteById(1)

        val result = profileDao.getById(1)
        assertNull(result)
    }
}
