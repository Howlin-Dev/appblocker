package com.serhii.appblocker.profiles.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.serhii.appblocker.core.domain.model.AppInfo
import com.serhii.appblocker.core.presentation.component.ConfirmDialog
import com.serhii.appblocker.core.presentation.scaffold.AppScaffold
import com.serhii.appblocker.profiles.presentation.common.ProfileAppIconGrid
import com.serhii.appblocker.profiles.presentation.detail.component.RenameProfileDialog
import com.serhii.appblocker.profiles.presentation.list.component.ProfileDetailAction
import com.serhii.appblocker.profiles.presentation.list.model.ProfileUi
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileDetailScreen(
    profileId: Long,
    onBackClick: () -> Unit,
    onManageListClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileDetailViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ProfileDetailScreenContent(
        modifier = modifier,
        profile = state.profile,
        onAction = { action ->
            when (action) {
                ProfileDetailAction.BackClick -> onBackClick()
                ProfileDetailAction.DeleteProfile -> viewModel.deleteProfile()
                ProfileDetailAction.ManageListClick -> onManageListClick(profileId)
                is ProfileDetailAction.ProfileNameChanged -> viewModel.updateProfileName(action.name)
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.getProfile(profileId)
    }
    LaunchedEffect(state.isProfileDeleted) {
        if (state.isProfileDeleted) {
            onBackClick()
        }
    }
}

@Composable
private fun ProfileDetailScreenContent(
    profile: ProfileUi?,
    onAction: (ProfileDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val renameDialogShown = remember { mutableStateOf(false) }
    val deleteConfirmDialogShown = remember { mutableStateOf(false) }

    AppScaffold(
        modifier = modifier,
        title = "Profile",
        onBackClick = { onAction(ProfileDetailAction.BackClick) },
        actions = {
            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options"
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Delete") },
                    onClick = {
                        expanded = false
                        deleteConfirmDialogShown.value = true
                    }
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileNameSection(
                name = profile?.name.orEmpty(),
                onRenameClick = { renameDialogShown.value = true }
            )
            ProfileAppListSection(
                appList = profile?.blockedApps.orEmpty(),
                onAction = onAction,
            )
        }
    }

    if (deleteConfirmDialogShown.value) {
        ConfirmDialog(
            onConfirm = {
                onAction(ProfileDetailAction.DeleteProfile)
                deleteConfirmDialogShown.value = false
            },
            onCancel = { deleteConfirmDialogShown.value = false },
            title = "Delete profile?",
            text = "The action cannot be undone",
            confirmButtonText = "Delete",
            cancelButtonText = "Cancel",
        )
    }
    if (renameDialogShown.value) {
        RenameProfileDialog(
            name = profile?.name.orEmpty(),
            onChange = {
                onAction(ProfileDetailAction.ProfileNameChanged(it))
                renameDialogShown.value = false
            },
            onCancel = { renameDialogShown.value = false },
        )
    }
}

@Composable
private fun ProfileNameSection(
    name: String,
    onRenameClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "Profile Name",
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(
                onClick = onRenameClick
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit"
                )
            }
        }
    }
}

@Composable
private fun ProfileAppListSection(
    appList: List<AppInfo>,
    onAction: (ProfileDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Profile Apps",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            ProfileAppIconGrid(
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                appList = appList,
            )
            TextButton(
                onClick = { onAction(ProfileDetailAction.ManageListClick) },
            ) {
                Text("Manage App List")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Manage List"
                )
            }
        }
    }
}

@Preview
@Composable
private fun ProfileDetailScreenPreview() {
    ProfileDetailScreenContent(
        profile = ProfileUi(
            name = "Reading"
        ),
        onAction = { },
    )
}
