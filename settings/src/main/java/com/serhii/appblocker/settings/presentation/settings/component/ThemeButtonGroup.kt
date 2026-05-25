package com.serhii.appblocker.settings.presentation.settings.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.serhii.appblocker.core.domain.model.ThemeMode

@Composable
fun ThemeButtonGroup(
    selected: ThemeMode,
    onSelected: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        ThemeMode.entries.forEachIndexed { index, mode ->

            val isSelected = selected == mode
            val isFirst = index == 0
            val isLast = index == ThemeMode.entries.size - 1

            val startCorner by animateDpAsState(
                targetValue = if (isFirst || isSelected) 24.dp else 8.dp,
                label = "startCorner",
            )
            val endCorner by animateDpAsState(
                targetValue = if (isLast || isSelected) 24.dp else 8.dp,
                label = "endCorner",
            )

            Surface(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                onClick = {
                    onSelected(mode)
                },
                shape = RoundedCornerShape(
                    topStart = startCorner,
                    bottomStart = startCorner,
                    topEnd = endCorner,
                    bottomEnd = endCorner,
                ),
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceContainerHighest
                },
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(mode.titleRes),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}
