package com.serhii.appblocker.profiles.data.repository

import app.cash.turbine.test
import com.serhii.appblocker.profiles.data.db.ProfileDao
import com.serhii.appblocker.profiles.data.model.ProfileEntity
import com.serhii.appblocker.profiles.domain.model.Profile
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ProfilesRepositoryImplTest {

    private val profileDao = mockk<ProfileDao>()
    private lateinit var repository: ProfilesRepositoryImpl

    @Before
    fun setup() {
        repository = ProfilesRepositoryImpl(profileDao)
    }

    @Test
    fun `getAll should return mapped profiles from dao`() = runTest {
        val entities = listOf(
            ProfileEntity(1, "Work", "Desc", "pkg1", 1000L),
        )
        every { profileDao.getAll() } returns flowOf(entities)

        repository.getAll().test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Work", result[0].name)
            awaitComplete()
        }
    }

    @Test
    fun `getById should return mapped profile`() = runTest {
        val entity = ProfileEntity(1, "Work", "Desc", "pkg1", 1000L)
        coEvery { profileDao.getById(1) } returns entity

        val result = repository.getById(1)
        assertEquals("Work", result.name)
    }

    @Test(expected = NoSuchElementException::class)
    fun `getById should throw when profile not found`() = runTest {
        coEvery { profileDao.getById(1) } returns null
        repository.getById(1)
    }

    @Test
    fun `insert should delegate to dao`() = runTest {
        val profile = Profile(1, "Work", "Desc", listOf("pkg1"), 1000L)
        coEvery { profileDao.insert(any()) } returns Unit

        repository.insert(profile)

        coVerify { profileDao.insert(match { it.id == 1L && it.name == "Work" }) }
    }

    @Test
    fun `delete should delegate to dao`() = runTest {
        coEvery { profileDao.deleteById(1) } returns Unit

        repository.delete(1)

        coVerify { profileDao.deleteById(1) }
    }
}
