package com.serhii.appblocker.profiles.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.serhii.appblocker.profiles.domain.model.Profile

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val blockedAppsPackageNames: String,
    val durationMillis: Long?,
)

fun ProfileEntity.toDomain() = Profile(
    id = id,
    name = name,
    description = description,
    appPackages = if (blockedAppsPackageNames.isEmpty()) emptyList() else blockedAppsPackageNames.split(","),
    durationMillis = durationMillis,
)

fun Profile.toEntity() = ProfileEntity(
    id = id,
    name = name,
    description = description,
    blockedAppsPackageNames = appPackages.joinToString(","),
    durationMillis = durationMillis,
)
