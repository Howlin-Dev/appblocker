package com.howlindev.appblocker.profiles.presentation.create

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.howlindev.appblocker.core.domain.model.AppInfo
import com.howlindev.appblocker.core.presentation.scaffold.AppScaffold
import com.howlindev.appblocker.profiles.R
import com.howlindev.appblocker.profiles.presentation.common.InstalledAppGrid
import kotlinx.coroutines.launch
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
        suggestedWebsites = state.suggestedWebsites,
        isLoading = state.isLoading,
        selectedAppsPackages = state.selectedApps,
        selectedWebsites = state.selectedWebsites,
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

                is CreateProfileAction.WebsiteSelected -> {
                    viewModel.toggleWebsiteSelection(action.website)
                }

                is CreateProfileAction.CustomWebsiteAdded -> {
                    viewModel.addCustomWebsite(action.website)
                }

                is CreateProfileAction.WebsiteRemoved -> {
                    viewModel.removeSuggestedWebsite(action.website)
                }
            }
        },
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
    suggestedWebsites: List<String>,
    isLoading: Boolean,
    selectedAppsPackages: Set<String>,
    selectedWebsites: Set<String>,
    onAction: (CreateProfileAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    AppScaffold(
        modifier = modifier,
        title = stringResource(R.string.profiles_create_title),
        navigationIconImageVector = Icons.Default.Close,
        onBackClick = { onAction(CreateProfileAction.BackClick) },
        actions = {
            TextButton(
                onClick = { onAction(CreateProfileAction.CreateProfileClick) },
                enabled = name.isNotEmpty() && (selectedAppsPackages.isNotEmpty() || selectedWebsites.isNotEmpty()),
            ) {
                Text(stringResource(R.string.profiles_button_create))
            }
        },
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                value = name,
                placeholder = {
                    Text(text = stringResource(R.string.profiles_name_placeholder))
                },
                onValueChange = { onAction(CreateProfileAction.NameChange(it)) },
                label = {
                    Text(stringResource(R.string.profiles_name_label))
                },
            )

            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    },
                    text = { Text("Apps") },
                )
                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    text = { Text("Websites") },
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) { page ->
                when (page) {
                    0 -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 8.dp),
                                text = stringResource(R.string.profiles_select_apps_description),
                                style = MaterialTheme.typography.titleSmall,
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                            ) {
                                InstalledAppGrid(
                                    modifier = Modifier.fillMaxWidth(),
                                    isLoading = isLoading,
                                    installedApps = installedApps,
                                    selectedAppsPackages = selectedAppsPackages,
                                    onItemClick = { onAction(CreateProfileAction.AppSelected(it)) },
                                    columnCount = 4,
                                )
                            }
                        }
                    }

                    1 -> {
                        WebsitesPage(
                            suggestedWebsites = suggestedWebsites,
                            selectedWebsites = selectedWebsites,
                            onAction = onAction,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WebsitesPage(
    suggestedWebsites: List<String>,
    selectedWebsites: Set<String>,
    onAction: (CreateProfileAction) -> Unit,
) {
    var websiteInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = websiteInput,
                onValueChange = { websiteInput = it },
                label = { Text("Add Website (e.g. example.com)") },
                singleLine = true,
            )
            IconButton(
                onClick = {
                    if (websiteInput.isNotBlank()) {
                        onAction(CreateProfileAction.CustomWebsiteAdded(websiteInput.trim()))
                        websiteInput = ""
                    }
                },
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Website")
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(suggestedWebsites) { website ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAction(CreateProfileAction.WebsiteSelected(website)) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Checkbox(
                            checked = selectedWebsites.contains(website),
                            onCheckedChange = { onAction(CreateProfileAction.WebsiteSelected(website)) }
                        )
                        Text(text = website, modifier = Modifier.padding(start = 8.dp))
                    }
                    
                    IconButton(onClick = { onAction(CreateProfileAction.WebsiteRemoved(website)) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Remove Suggested Website")
                    }
                }
                HorizontalDivider()
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
            suggestedWebsites = listOf("facebook.com", "instagram.com"),
            selectedAppsPackages = emptySet(),
            selectedWebsites = emptySet(),
            onAction = { },
            isLoading = true,
        )
    }
}
