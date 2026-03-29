package com.serhii.appblocker.profiles.presentation.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.serhii.appblocker.core.domain.model.AppInfo
import com.serhii.appblocker.core.presentation.scaffold.AppScaffold
import com.serhii.appblocker.profiles.presentation.create.component.AppItem
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
        selectedAppsPackages = state.selectedApps,
        onAction = { action ->
            when(action) {
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
        if(state.isCreated) {
            onBackClick()
        }
    }
}

@Composable
private fun CreateProfileScreenContent(
    name: String,
    installedApps: List<AppInfo>,
    selectedAppsPackages: Set<String>,
    onAction: (CreateProfileAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    AppScaffold(
        modifier = modifier,
        title = "New Profile",
        onBackClick = { onAction(CreateProfileAction.BackClick) }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                value = name,
                placeholder = {
                    Text(text = "Profile Name...")
                },
                onValueChange = { onAction(CreateProfileAction.NameChange(it)) }
            )
            Box(
                modifier = Modifier.weight(1f)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(items = installedApps) {
                        AppItem(
                            app = it,
                            selected = selectedAppsPackages.contains(it.packageName),
                            onClick = { onAction(CreateProfileAction.AppSelected(it.packageName)) }
                        )
                    }
                }
                Button(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .height(56.dp)
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    onClick = {
                        onAction(CreateProfileAction.CreateProfileClick)
                    },
                    enabled = name.isNotEmpty() && selectedAppsPackages.isNotEmpty()
                ) {
                    Text(
                        text = "Create",
                        style = MaterialTheme.typography.labelLarge,
                        fontSize = 20.sp
                    )
                }
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
            onAction = { }
        )
    }
}