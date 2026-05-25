package com.serhii.appblocker.profiles.data.model

import com.serhii.appblocker.profiles.domain.model.Profile
import org.junit.Assert.assertEquals
import org.junit.Test

class ProfileMappingTest {

    @Test
    fun `ProfileEntity toDomain should correctly map fields`() {
        val entity = ProfileEntity(
            id = 1,
            name = "Work",
            description = "Work desc",
            blockedAppsPackageNames = "pkg1,pkg2",
            durationMillis = 1000L,
        )

        val domain = entity.toDomain()

        assertEquals(entity.id, domain.id)
        assertEquals(entity.name, domain.name)
        assertEquals(entity.description, domain.description)
        assertEquals(listOf("pkg1", "pkg2"), domain.appPackages)
        assertEquals(entity.durationMillis, domain.durationMillis)
    }

    @Test
    fun `ProfileEntity toDomain with empty apps should return empty list`() {
        val entity = ProfileEntity(
            id = 1,
            name = "Work",
            description = "Work desc",
            blockedAppsPackageNames = "",
            durationMillis = null,
        )

        val domain = entity.toDomain()

        assertEquals(emptyList<String>(), domain.appPackages)
    }

    @Test
    fun `Profile toEntity should correctly map fields`() {
        val domain = Profile(
            id = 1,
            name = "Work",
            description = "Work desc",
            appPackages = listOf("pkg1", "pkg2"),
            durationMillis = 1000L,
        )

        val entity = domain.toEntity()

        assertEquals(domain.id, entity.id)
        assertEquals(domain.name, entity.name)
        assertEquals(domain.description, entity.description)
        assertEquals("pkg1,pkg2", entity.blockedAppsPackageNames)
        assertEquals(domain.durationMillis, entity.durationMillis)
    }
}
