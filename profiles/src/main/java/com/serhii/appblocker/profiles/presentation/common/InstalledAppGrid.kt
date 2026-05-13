package com.serhii.appblocker.profiles.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.serhii.appblocker.core.domain.model.AppInfo
import com.serhii.appblocker.profiles.presentation.create.component.AppItem

@Composable
fun InstalledAppGrid(
    installedApps: List<AppInfo>,
    selectedAppsPackages: Set<String>,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    columnCount: Int = 4,
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(columnCount),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(items = installedApps) {
            AppItem(
                app = it,
                selected = selectedAppsPackages.contains(it.packageName),
                onClick = { onItemClick(it.packageName) }
            )
        }
    }
}