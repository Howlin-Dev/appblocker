package com.howlindev.appblocker.profiles.presentation.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.howlindev.appblocker.core.domain.model.AppInfo
import com.howlindev.appblocker.core.presentation.scaffold.AppScaffold
import com.howlindev.appblocker.profiles.R
import com.howlindev.appblocker.profiles.presentation.common.InstalledAppGrid
import com.howlindev.appblocker.profiles.presentation.common.WebsiteSelectionList
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 16.dp),
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
                    text = { Text(stringResource(R.string.profiles_tab_apps)) },
                )
                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    text = { Text(stringResource(R.string.profiles_tab_websites)) },
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
                        WebsiteSelectionList(
                            suggestedWebsites = suggestedWebsites,
                            selectedWebsites = selectedWebsites,
                            onWebsiteSelected = { onAction(CreateProfileAction.WebsiteSelected(it)) },
                            onCustomWebsiteAdded = { onAction(CreateProfileAction.CustomWebsiteAdded(it)) },
                            onWebsiteRemoved = { onAction(CreateProfileAction.WebsiteRemoved(it)) },
                        )
                    }
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
            suggestedWebsites = listOf("facebook.com", "instagram.com"),
            selectedAppsPackages = emptySet(),
            selectedWebsites = emptySet(),
            onAction = { },
            isLoading = true,
        )
    }
}
