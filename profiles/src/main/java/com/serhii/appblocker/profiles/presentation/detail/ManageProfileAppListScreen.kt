package com.serhii.appblocker.profiles.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
fun ManageProfileAppListScreen(
    profileId: Long,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ManageProfileAppListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ManageProfileAppListScreenContent(
        modifier = modifier,
        isLoading = state.isLoading,
        installedApps = state.installedApps,
        selectedAppsPackages = state.selectedApps,
        onAction = { action ->
            when (action) {
                is ManageProfileAppListAction.AppSelected -> {
                    viewModel.toggleAppSelection(action.packageName)
                }

                ManageProfileAppListAction.BackClick -> {
                    onBackClick()
                }

                ManageProfileAppListAction.ApplyClick -> {
                    viewModel.applyChanges()
                }
            }
        },
    )

    LaunchedEffect(Unit) {
        viewModel.getProfile(profileId)
    }
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onBackClick()
        }
    }
}

@Composable
private fun ManageProfileAppListScreenContent(
    isLoading: Boolean,
    installedApps: List<AppInfo>,
    selectedAppsPackages: Set<String>,
    onAction: (ManageProfileAppListAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    AppScaffold(
        modifier = modifier,
        title = "Manage App List",
        navigationIconImageVector = Icons.Default.Close,
        onBackClick = { onAction(ManageProfileAppListAction.BackClick) },
        actions = {
            TextButton(
                onClick = { onAction(ManageProfileAppListAction.ApplyClick) },
                enabled = selectedAppsPackages.isNotEmpty(),
            ) {
                Text("Apply")
            }
        },
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            Box(
                modifier = Modifier.weight(1f),
            ) {
                InstalledAppGrid(
                    isLoading = isLoading,
                    installedApps = installedApps,
                    selectedAppsPackages = selectedAppsPackages,
                    onItemClick = { onAction(ManageProfileAppListAction.AppSelected(it)) },
                    columnCount = 4,
                )
            }
        }
    }
}

@Preview
@Composable
private fun ProfileListScreenScreenPreview() {
    Surface {
        ManageProfileAppListScreenContent(
            isLoading = true,
            installedApps = emptyList(),
            selectedAppsPackages = emptySet(),
            onAction = { },
        )
    }
}
