package com.serhii.appblocker.profiles.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.serhii.appblocker.core.presentation.component.ConfirmDialog
import com.serhii.appblocker.core.presentation.scaffold.AppScaffold
import com.serhii.appblocker.core.util.formatMillis
import com.serhii.appblocker.profiles.presentation.list.component.ActiveProfileListItem
import com.serhii.appblocker.profiles.presentation.list.component.ProfileDetailAction
import com.serhii.appblocker.profiles.presentation.list.component.ProfileListItem
import com.serhii.appblocker.profiles.presentation.list.model.ProfileUi
import com.serhii.appblocker.profiles.util.millisToTimeString
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileListScreen(
    onCreateClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onProfileClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val remainingMillis by viewModel.remainingTime.collectAsState()
    var pendingProfileForActivation by remember { mutableStateOf<ProfileUi?>(null) }

    val formattedTime = remember(remainingMillis) {
        formatMillis(remainingMillis)
    }

    ProfileListScreenContent(
        modifier = modifier.fillMaxSize(),
        inactiveProfiles = state.inactiveProfiles,
        activeProfile = state.activeProfile,
        formattedTimeRemaining = formattedTime,
        onAction = { action ->
            when (action) {
                ProfileListAction.CreateClick -> onCreateClick()
                ProfileListAction.SettingsClick -> onSettingsClick()
                is ProfileListAction.ProfileClick -> onProfileClick(action.id)
                is ProfileListAction.ToggleProfileActivation -> {
                    if (action.profile.durationMillis == null)
                        viewModel.toggleProfileActivation(action.profile)
                    else
                        pendingProfileForActivation = action.profile
                }

                is ProfileListAction.TimerChange -> viewModel.updateProfileTimer(
                    action.profileUi,
                    action.newTime
                )
            }
        }
    )

    if (pendingProfileForActivation != null) {
        ConfirmDialog(
            onConfirm = {
                pendingProfileForActivation?.let { viewModel.toggleProfileActivation(it) }
                pendingProfileForActivation = null
            },
            onCancel = { pendingProfileForActivation = null },
            title = "Activate ${pendingProfileForActivation?.name}?",
            text = "Profile apps will be blocked for ${pendingProfileForActivation?.durationMillis?.millisToTimeString()}",
            confirmButtonText = "Activate",
            cancelButtonText = "Cancel",
        )
    }
}

@Composable
private fun ProfileListScreenContent(
    inactiveProfiles: List<ProfileUi>,
    activeProfile: ProfileUi?,
    formattedTimeRemaining: String,
    onAction: (ProfileListAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    AppScaffold(
        modifier = modifier,
        title = "Bloq",
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick = { onAction(ProfileListAction.CreateClick) }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        },
        actions = {
            IconButton(
                onClick = { onAction(ProfileListAction.SettingsClick) }
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
        },
    ) {
        LazyColumn(
            modifier = Modifier,
            contentPadding = PaddingValues(bottom = 140.dp, top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            activeProfile?.let { activeProfile ->
                item {
                    ActiveProfileListItem(
                        modifier = Modifier.animateItem(),
                        profile = activeProfile,
                        onUnblockClick = {
                            onAction(
                                ProfileListAction.ToggleProfileActivation(
                                    activeProfile
                                )
                            )
                        },
                        formattedTimeRemaining = formattedTimeRemaining
                    )
                }
            }
            items(items = inactiveProfiles) { inactiveProfile ->
                ProfileListItem(
                    modifier = Modifier.animateItem(),
                    profile = inactiveProfile,
                    onClick = { onAction(ProfileListAction.ProfileClick(inactiveProfile.id)) },
                    onToggleProfileActivation = {
                        onAction(ProfileListAction.ToggleProfileActivation(inactiveProfile))
                    },
                    isAnotherProfileActive = activeProfile != null,
                    onTimerChanged = { newTime ->
                        onAction(
                            ProfileListAction.TimerChange(
                                profileUi = inactiveProfile,
                                newTime = newTime
                            )
                        )
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun ProfileListScreenPreview() {
    Surface {
        ProfileListScreenContent(
            inactiveProfiles = emptyList(),
            onAction = {},
            activeProfile = null,
            formattedTimeRemaining = "",
        )
    }
}