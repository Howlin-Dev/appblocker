package com.serhii.appblocker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.appblocker.permissions.presentation.PermissionsScreen
import com.serhii.appblocker.navigation.entry.EntryViewModel
import com.serhii.appblocker.profiles.presentation.create.CreateProfileScreen
import com.serhii.appblocker.profiles.presentation.list.ProfileListScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: EntryViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = ProfileListDestination,
        modifier = modifier,
    ) {
        composable<ProfileListDestination> {
            ProfileListScreen(
                onCreateClick = { navController.navigate(CreateProfileDestination) },
                onProfileClick = { },
            )
        }
        composable<CreateProfileDestination> {
            CreateProfileScreen(
                onBackClick = { navController.popBackStack() },
            )
        }
        composable<PermissionsDestination> {
            PermissionsScreen(
                onAllPermissionsGranted = {
                    navController.navigate(ProfileListDestination) {
                        popUpTo(PermissionsDestination) { inclusive = true }
                    }
                }
            )
        }
    }

    LaunchedEffect(state.arePermissionsNeeded) {
        if (state.arePermissionsNeeded == true) {
            navController.navigate(PermissionsDestination) {
                popUpTo(ProfileListDestination) { inclusive = true }
            }
        }
    }
}

@Serializable
object PermissionsDestination

@Serializable
object ProfileListDestination

@Serializable
object CreateProfileDestination
