package com.howlindev.appblocker.navigation

import kotlinx.serialization.Serializable

@Serializable
object PermissionsDestination

@Serializable
object ProfileListDestination

@Serializable
object CreateProfileDestination

@Serializable
data class ProfileDetailDestination(
    val profileId: Long,
)

@Serializable
data class ManageProfileAppListDestination(
    val profileId: Long,
)

@Serializable
object SettingsDestination

@Serializable
object LanguageDestination

