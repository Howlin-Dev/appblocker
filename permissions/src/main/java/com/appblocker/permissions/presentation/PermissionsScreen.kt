package com.appblocker.permissions.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appblocker.permissions.domain.model.RequiredPermission
import com.serhii.appblocker.core.presentation.scaffold.AppScaffold
import org.koin.androidx.compose.koinViewModel

@Composable
fun PermissionsScreen(
    onAllPermissionsGranted: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PermissionsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LifecycleResumeEffect(viewModel) {
        viewModel.checkPermissions()
        onPauseOrDispose { }
    }

    PermissionsScreenContent(
        modifier = modifier,
        missingPermissions = state.missingRequiredPermissions,
        onAction = { action ->
            when (action) {
                is PermissionsAction.PermissionGrantClick -> {
                    viewModel.requestPermission(action.permission)
                }
            }
        }
    )

    LaunchedEffect(state.allPermissionsGranted) {
        if (state.allPermissionsGranted) {
            onAllPermissionsGranted()
        }
    }
}

@Composable
private fun PermissionsScreenContent(
    missingPermissions: List<RequiredPermission>,
    onAction: (PermissionsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    AppScaffold(
        modifier = modifier,
    ) {
        LazyColumn(
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                PermissionScreenInfo()
            }
            items(items = missingPermissions) {
                PermissionItem(
                    title = it.title,
                    subtitle = it.subtitle,
                    onClick = { onAction(PermissionsAction.PermissionGrantClick(it)) }
                )
            }
        }
    }
}

@Composable
private fun PermissionScreenInfo(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = "Some permissions are needed for the application to work properly",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "Some permissions are needed for the application to work properly",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
private fun PermissionsScreenContentPreview() {
    Surface {
        PermissionsScreenContent(
            modifier = Modifier.fillMaxSize(),
            missingPermissions = listOf(
                RequiredPermission.Overlay,
                RequiredPermission.UsageAccess,
            ),
            onAction = {}
        )
    }
}
