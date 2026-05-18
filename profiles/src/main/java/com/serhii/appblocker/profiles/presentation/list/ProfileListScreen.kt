package com.serhii.appblocker.profiles.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.serhii.appblocker.core.presentation.scaffold.AppScaffold
import com.serhii.appblocker.core.util.formatMillis
import com.serhii.appblocker.profiles.presentation.list.component.ProfileListItem
import com.serhii.appblocker.profiles.presentation.list.model.ProfileUi
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileListScreen(
    onCreateClick: () -> Unit,
    onProfileClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val remainingMillis by viewModel.remainingTime.collectAsState()

    val formattedTime = remember(remainingMillis) {
        formatMillis(remainingMillis)
    }

    ProfileListScreenContent(
        modifier = modifier.fillMaxSize(),
        profiles = state.profiles,
        activeProfileId = state.activeProfileId,
        formattedTimeRemaining = formattedTime,
        onAction = { action ->
            when (action) {
                ProfileListAction.CreateClick -> onCreateClick()
                is ProfileListAction.ProfileClick -> onProfileClick(action.id)
                is ProfileListAction.ToggleProfileActivation -> viewModel.toggleProfileActivation(
                    action.profile
                )
                is ProfileListAction.TimerChange -> viewModel.updateProfileTimer(action.profileUi, action.newTime)
            }
        }
    )
}

@Composable
private fun ProfileListScreenContent(
    profiles: List<ProfileUi>,
    activeProfileId: Long?,
    formattedTimeRemaining: String,
    onAction: (ProfileListAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    AppScaffold(
        modifier = modifier,
        title = "Profiles",
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick = { onAction(ProfileListAction.CreateClick) }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        },
        actions = {
            IconButton(
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
        },
    ) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 140.dp, top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items = profiles) {
                ProfileListItem(
                    profile = it,
                    onClick = { onAction(ProfileListAction.ProfileClick(it.id)) },
                    onToggleProfileActivation = {
                        onAction(
                            ProfileListAction.ToggleProfileActivation(
                                it
                            )
                        )
                    },
                    isActive = activeProfileId == it.id,
                    isAnotherProfileActive = activeProfileId != it.id && activeProfileId != null,
                    formattedTimeRemaining = formattedTimeRemaining,
                    onTimerChanged = { newTime -> onAction(ProfileListAction.TimerChange(it, newTime)) }
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
            profiles = emptyList(),
            onAction = {},
            activeProfileId = null,
            formattedTimeRemaining = "",
        )
    }
}