package com.serhii.appblocker.profiles.presentation.detail

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.serhii.appblocker.core.presentation.scaffold.AppScaffold
import com.serhii.appblocker.profiles.presentation.detail.component.DeleteProfileConfirmDialog
import com.serhii.appblocker.profiles.presentation.detail.component.ProfileDetailAppIconGrid
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
    var renameDialogShown by remember { mutableStateOf(false) }
    var deleteConfirmDialogShown by remember { mutableStateOf(false) }

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
                        deleteConfirmDialogShown = true
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
                onRenameClick = { renameDialogShown = true }
            )
            ProfileAppListSection(
                appList = profile?.blockedApps.orEmpty(),
                onAction = onAction,
            )
        }
    }

    if (deleteConfirmDialogShown) {
        DeleteProfileConfirmDialog(
            onConfirm = {
                onAction(ProfileDetailAction.DeleteProfile)
                deleteConfirmDialogShown = false
            },
            onCancel = { deleteConfirmDialogShown = false }
        )
    }
    if (renameDialogShown) {
        RenameProfileDialog(
            name = profile?.name.orEmpty(),
            onChange = {
                onAction(ProfileDetailAction.ProfileNameChanged(it))
                renameDialogShown = false
            },
            onCancel = { renameDialogShown = false },
        )
    }
}

//@Composable
//private fun ProfileNameSection(
//    name: String,
//    onValueChange: (String) -> Unit,
//    modifier: Modifier = Modifier,
//) {
//    OutlinedTextField(
//        modifier = modifier,
//        value = name,
//        onValueChange = onValueChange,
//        singleLine = true,
//        maxLines = 1,
//        placeholder = {
//            Text(text = "Name")
//        },
//    )
//}

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
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
    Card(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Profile Apps:",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            ProfileDetailAppIconGrid(
                modifier = Modifier.padding(top = 24.dp, bottom = 16.dp),
                appList = appList,
            )
            TextButton(
                onClick = { onAction(ProfileDetailAction.ManageListClick) },
            ) {
                Text("Manage List")
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
        profile = null,
        onAction = { },
    )
}