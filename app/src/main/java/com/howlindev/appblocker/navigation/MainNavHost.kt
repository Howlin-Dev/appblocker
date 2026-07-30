package com.howlindev.appblocker.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.howlindev.appblocker.permissions.presentation.PermissionsScreen
import com.howlindev.appblocker.profiles.presentation.create.CreateProfileScreen
import com.howlindev.appblocker.profiles.presentation.detail.ManageProfileAppListScreen
import com.howlindev.appblocker.profiles.presentation.detail.ManageProfileWebsiteListScreen
import com.howlindev.appblocker.profiles.presentation.detail.ProfileDetailScreen
import com.howlindev.appblocker.profiles.presentation.list.ProfileListScreen
import com.howlindev.appblocker.schedule.presentation.SchedulerScreen
import com.howlindev.appblocker.settings.presentation.language.LanguageScreen
import com.howlindev.appblocker.settings.presentation.settings.SettingsScreen

@Composable
fun MainNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val startDestination = ProfileListDestination

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.92f, animationSpec = tween(300))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.92f, animationSpec = tween(300))
        },
    ) {
        composable<ProfileListDestination> {
            ProfileListScreen(
                onCreateClick = { navController.navigateSafe(CreateProfileDestination) },
                onProfileClick = { navController.navigateSafe(ProfileDetailDestination(it)) },
                onSettingsClick = { navController.navigateSafe(SettingsDestination) },
                onSchedulerClick = { navController.navigateSafe(SchedulerDestination) },
            )
        }
        composable<CreateProfileDestination> {
            CreateProfileScreen(
                onBackClick = { profileId ->
                    if (profileId != null) {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("new_profile_id", profileId)
                    }
                    navController.popBackStackSafe()
                },
            )
        }
        composable<ProfileDetailDestination> {
            val destination = it.toRoute<ProfileDetailDestination>()
            ProfileDetailScreen(
                profileId = destination.profileId,
                onBackClick = { navController.popBackStackSafe() },
                onManageAppListClick = { navController.navigateSafe(ManageProfileAppListDestination(it)) },
                onManageWebsiteListClick = { navController.navigateSafe(ManageProfileWebsiteListDestination(it)) },
            )
        }
        composable<ManageProfileAppListDestination> {
            val destination = it.toRoute<ManageProfileAppListDestination>()
            ManageProfileAppListScreen(
                profileId = destination.profileId,
                onBackClick = { navController.popBackStackSafe() },
            )
        }
        composable<ManageProfileWebsiteListDestination> {
            val destination = it.toRoute<ManageProfileWebsiteListDestination>()
            ManageProfileWebsiteListScreen(
                profileId = destination.profileId,
                onBackClick = { navController.popBackStackSafe() },
            )
        }
        composable<PermissionsDestination> {
            PermissionsScreen(
                onAllPermissionsGranted = {
                    navController.navigate(ProfileListDestination) {
                        popUpTo(PermissionsDestination) { inclusive = true }
                    }
                },
            )
        }
        composable<SettingsDestination> {
            SettingsScreen(
                onBackClick = { navController.popBackStackSafe() },
                onLanguageClick = { navController.navigateSafe(LanguageDestination) },
            )
        }
        composable<LanguageDestination> {
            LanguageScreen(
                onBackClick = { navController.popBackStackSafe() },
            )
        }
        composable<SchedulerDestination> {
            val profileId = it.savedStateHandle.get<Long>("new_profile_id")
            LaunchedEffect(profileId) {
                if (profileId != null) {
                    it.savedStateHandle.remove<Long>("new_profile_id")
                }
            }
            SchedulerScreen(
                onBackClick = { navController.popBackStackSafe() },
                onProfileClick = { navController.navigateSafe(ProfileDetailDestination(it)) },
                onCreateProfileClick = { navController.navigateSafe(CreateProfileDestination) },
                newProfileId = profileId,
            )
        }
    }
}
