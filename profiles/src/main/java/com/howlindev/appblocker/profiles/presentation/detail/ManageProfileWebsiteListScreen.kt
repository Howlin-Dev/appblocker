package com.howlindev.appblocker.profiles.presentation.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.howlindev.appblocker.core.presentation.scaffold.AppScaffold
import com.howlindev.appblocker.profiles.R
import com.howlindev.appblocker.profiles.presentation.common.WebsiteSelectionList
import org.koin.androidx.compose.koinViewModel

@Composable
fun ManageProfileWebsiteListScreen(
    profileId: Long,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ManageProfileWebsiteListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ManageProfileWebsiteListScreenContent(
        modifier = modifier,
        isLoading = state.isLoading,
        suggestedWebsites = state.suggestedWebsites,
        selectedWebsites = state.selectedWebsites,
        onAction = { action ->
            when (action) {
                is ManageProfileWebsiteListAction.WebsiteSelected -> {
                    viewModel.toggleWebsiteSelection(action.website)
                }
                is ManageProfileWebsiteListAction.CustomWebsiteAdded -> {
                    viewModel.addCustomWebsite(action.website)
                }
                is ManageProfileWebsiteListAction.WebsiteRemoved -> {
                    viewModel.removeSuggestedWebsite(action.website)
                }
                ManageProfileWebsiteListAction.BackClick -> {
                    onBackClick()
                }
                ManageProfileWebsiteListAction.ApplyClick -> {
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
private fun ManageProfileWebsiteListScreenContent(
    isLoading: Boolean,
    suggestedWebsites: List<String>,
    selectedWebsites: Set<String>,
    onAction: (ManageProfileWebsiteListAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    AppScaffold(
        modifier = modifier,
        title = stringResource(R.string.profiles_manage_website_list_button),
        navigationIconImageVector = Icons.Default.Close,
        onBackClick = { onAction(ManageProfileWebsiteListAction.BackClick) },
        actions = {
            TextButton(
                onClick = { onAction(ManageProfileWebsiteListAction.ApplyClick) },
                enabled = selectedWebsites.isNotEmpty(),
            ) {
                Text(stringResource(R.string.profiles_button_apply))
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues),
        ) {
            WebsiteSelectionList(
                suggestedWebsites = suggestedWebsites,
                selectedWebsites = selectedWebsites,
                onWebsiteSelected = { onAction(ManageProfileWebsiteListAction.WebsiteSelected(it)) },
                onCustomWebsiteAdded = { onAction(ManageProfileWebsiteListAction.CustomWebsiteAdded(it)) },
                onWebsiteRemoved = { onAction(ManageProfileWebsiteListAction.WebsiteRemoved(it)) },
            )
        }
    }
}

@Preview
@Composable
private fun ManageProfileWebsiteListScreenPreview() {
    Surface {
        ManageProfileWebsiteListScreenContent(
            isLoading = false,
            suggestedWebsites = listOf("facebook.com", "instagram.com"),
            selectedWebsites = setOf("facebook.com"),
            onAction = { },
        )
    }
}
