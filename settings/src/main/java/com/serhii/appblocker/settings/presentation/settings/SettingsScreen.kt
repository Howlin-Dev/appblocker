package com.serhii.appblocker.settings.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.serhii.appblocker.core.domain.model.ThemeMode
import com.serhii.appblocker.core.presentation.scaffold.AppScaffold
import com.serhii.appblocker.settings.R
import com.serhii.appblocker.settings.platform.appVersion
import com.serhii.appblocker.settings.presentation.settings.component.ThemeButtonGroup
import org.koin.androidx.compose.koinViewModel
import com.serhii.appblocker.core.R as CoreRes

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onLanguageClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    SettingsScreenContent(
        modifier = modifier,
        currentThemeMode = settings.themeMode,
        onAction = { action ->
            when (action) {
                SettingsAction.BackClick -> {
                    onBackClick()
                }

                SettingsAction.LanguageClick -> {
                    onLanguageClick()
                }

                is SettingsAction.ThemeModeSwitch -> {
                    viewModel.setThemeMode(action.themeMode)
                }
            }
        },
    )
}

@Composable
private fun SettingsScreenContent(
    onAction: (SettingsAction) -> Unit,
    currentThemeMode: ThemeMode,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    AppScaffold(
        modifier = modifier,
        title = stringResource(R.string.settings),
        onBackClick = { onAction(SettingsAction.BackClick) },
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = modifier
                    .height(64.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Icon(
                    painter = painterResource(CoreRes.drawable.outline_day_night),
                    contentDescription = "Theme",
                )
                Text(
                    text = stringResource(R.string.theme),
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.width(8.dp))
                ThemeButtonGroup(
                    modifier = Modifier.height(40.dp),
                    selected = currentThemeMode,
                    onSelected = { onAction(SettingsAction.ThemeModeSwitch(it)) },
                )
            }
            Surface(
                modifier = modifier
                    .height(64.dp)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(8.dp),
                onClick = { onAction(SettingsAction.LanguageClick) },
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Icon(
                        painter = painterResource(CoreRes.drawable.outline_language),
                        contentDescription = "Language",
                    )
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.language),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                        contentDescription = "Language Navigate",
                    )
                }
            }
            Text(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .alpha(0.5f),
                text = stringResource(R.string.app_version, context.appVersion()),
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    Surface {
        SettingsScreenContent(
            onAction = { },
            currentThemeMode = ThemeMode.LIGHT,
        )
    }
}
