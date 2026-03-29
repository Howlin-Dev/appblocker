package com.serhii.appblocker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.serhii.appblocker.navigation.entry.EntryViewModel
import com.serhii.appblocker.presentation.permissions.PermissionsScreen
import com.serhii.appblocker.profiles.presentation.list.ProfileListScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: EntryViewModel = koinViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = ProfileListDestination,
        modifier = modifier,
    ) {
        composable<ProfileListDestination> {
            ProfileListScreen(
                onAddClick = {  },
                onProfileClick = {  },
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

    LaunchedEffect(state.value.arePermissionsNeeded) {
        if(state.value.arePermissionsNeeded == true) {
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
