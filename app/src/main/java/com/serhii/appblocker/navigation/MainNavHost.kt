package com.serhii.appblocker.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.appblocker.permissions.presentation.PermissionsScreen
import com.serhii.appblocker.navigation.entry.EntryViewModel
import com.serhii.appblocker.profiles.presentation.create.CreateProfileScreen
import com.serhii.appblocker.profiles.presentation.detail.ManageProfileAppListScreen
import com.serhii.appblocker.profiles.presentation.detail.ProfileDetailScreen
import com.serhii.appblocker.profiles.presentation.list.ProfileListScreen
import com.serhii.appblocker.settings.presentation.language.LanguageScreen
import com.serhii.appblocker.settings.presentation.settings.SettingsScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: EntryViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val arePermissionsNeeded = state.arePermissionsNeeded

    if (arePermissionsNeeded == null) {
        return
    }

    val startDestination = remember {
        if (arePermissionsNeeded == true) {
            PermissionsDestination
        } else {
            ProfileListDestination
        }
    }

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
            )
        }
        composable<CreateProfileDestination> {
            CreateProfileScreen(
                onBackClick = {
                    navController.popBackStackSafe()
                },
            )
        }
        composable<ProfileDetailDestination> {
            val destination = it.toRoute<ProfileDetailDestination>()
            ProfileDetailScreen(
                profileId = destination.profileId,
                onBackClick = { navController.popBackStackSafe() },
                onManageListClick = { navController.navigateSafe(ManageProfileAppListDestination(it)) },
            )
        }
        composable<ManageProfileAppListDestination> {
            val destination = it.toRoute<ManageProfileAppListDestination>()
            ManageProfileAppListScreen(
                profileId = destination.profileId,
                onBackClick = { navController.popBackStackSafe() },
            )
        }
        composable<PermissionsDestination> {
            PermissionsScreen(
                onAllPermissionsGranted = {
//                    if (navController.currentBackStackEntry?.lifecycle?.currentState == androidx.lifecycle.Lifecycle.State.RESUMED) {
                    navController.navigate(ProfileListDestination) {
                        popUpTo(PermissionsDestination) { inclusive = true }
                    }
//                    }
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
    }

    LaunchedEffect(arePermissionsNeeded) {
        if (arePermissionsNeeded) {
            val currentBackStackEntry = navController.currentBackStackEntry
            val isAlreadyOnPermissions = currentBackStackEntry?.destination?.hasRoute<PermissionsDestination>() == true

            if (currentBackStackEntry != null && !isAlreadyOnPermissions) {
                navController.navigate(PermissionsDestination) {
                    popUpTo(ProfileListDestination) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }
}
