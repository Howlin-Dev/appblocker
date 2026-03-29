package com.serhii.appblocker.profiles.presentation.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.serhii.appblocker.profiles.domain.model.Profile
import com.serhii.appblocker.profiles.presentation.list.component.ProfileListItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileListScreen(
    onAddClick: () -> Unit,
    onProfileClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileListViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    ProfileListScreenContent(
        modifier = modifier,
        profiles = state.value.profiles,
        onAction = { action ->
            when (action) {
                ProfileListAction.AddClick -> onAddClick()
                is ProfileListAction.ProfileClick -> onProfileClick(action.id)
            }
        }
    )
}

@Composable
private fun ProfileListScreenContent(
    profiles: List<Profile>,
    onAction: (ProfileListAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        LazyColumn {
            items(items = profiles) {
                ProfileListItem(
                    profile = it,
                    onClick = { onAction(ProfileListAction.ProfileClick(it.id)) })
            }
        }
        FloatingActionButton(
            onClick = { onAction(ProfileListAction.AddClick) }
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
        }
    }
}

@Preview
@Composable
private fun ProfileListScreenPreview() {
    Surface {
        ProfileListScreen(
            onAddClick = { },
            onProfileClick = { },
        )
    }
}