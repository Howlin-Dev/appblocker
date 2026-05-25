package com.serhii.appblocker.profiles.presentation.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.serhii.appblocker.core.domain.model.AppInfo
import com.serhii.appblocker.core.presentation.scaffold.AppScaffold
import com.serhii.appblocker.profiles.presentation.common.InstalledAppGrid
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateProfileScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateProfileViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CreateProfileScreenContent(
        modifier = modifier,
        name = state.name,
        installedApps = state.installedApps,
        isLoading = state.isLoading,
        selectedAppsPackages = state.selectedApps,
        onAction = { action ->
            when (action) {
                is CreateProfileAction.AppSelected -> {
                    viewModel.toggleAppSelection(action.packageName)
                }

                CreateProfileAction.BackClick -> {
                    onBackClick()
                }

                CreateProfileAction.CreateProfileClick -> {
                    viewModel.createProfile()
                }

                is CreateProfileAction.NameChange -> {
                    viewModel.onNameChange(action.name)
                }
            }
        }
    )

    LaunchedEffect(state.isCreated) {
        if (state.isCreated) {
            onBackClick()
        }
    }
}

@Composable
private fun CreateProfileScreenContent(
    name: String,
    installedApps: List<AppInfo>,
    isLoading: Boolean,
    selectedAppsPackages: Set<String>,
    onAction: (CreateProfileAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    AppScaffold(
        modifier = modifier,
        title = "New Profile",
        navigationIconImageVector = Icons.Default.Close,
        onBackClick = { onAction(CreateProfileAction.BackClick) },
        actions = {
            TextButton(
                onClick = { onAction(CreateProfileAction.CreateProfileClick) },
                enabled = name.isNotEmpty() && selectedAppsPackages.isNotEmpty(),
            ) {
                Text("Create")
            }
        }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                value = name,
                placeholder = {
                    Text(text = "Profile Name...")
                },
                onValueChange = { onAction(CreateProfileAction.NameChange(it)) },
                label = {
                    Text("Profile Name")
                }
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                text = "Select apps you want to block upon activating this profile",
                style = MaterialTheme.typography.titleSmall,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                InstalledAppGrid(
                    modifier = Modifier
                        .fillMaxWidth(),
                    isLoading = isLoading,
                    installedApps = installedApps,
                    selectedAppsPackages = selectedAppsPackages,
                    onItemClick = { onAction(CreateProfileAction.AppSelected(it)) },
                    columnCount = 4
                )
            }
        }
    }
}

@Preview
@Composable
private fun ProfileListScreenPreview() {
    Surface {
        CreateProfileScreenContent(
            name = "Reading",
            installedApps = emptyList(),
            selectedAppsPackages = emptySet(),
            onAction = { },
            isLoading = true,
        )
    }
}
