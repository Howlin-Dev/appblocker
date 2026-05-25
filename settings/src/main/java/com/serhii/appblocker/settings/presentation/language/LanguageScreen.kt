package com.serhii.appblocker.settings.presentation.language

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.serhii.appblocker.core.domain.model.AppLanguage
import com.serhii.appblocker.core.presentation.scaffold.AppScaffold
import com.serhii.appblocker.settings.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun LanguageScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LanguageViewModel = koinViewModel(),
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    LanguageScreenContent(
        modifier = modifier,
        selectedItem = settings.language,
        onAction = { action ->
            when (action) {
                LanguageAction.BackClick -> {
                    onBackClick()
                }

                is LanguageAction.LanguageChange -> {
                    viewModel.setLanguage(action.language)
                }
            }
        },
    )
}

@Composable
private fun LanguageScreenContent(
    onAction: (LanguageAction) -> Unit,
    selectedItem: AppLanguage,
    modifier: Modifier = Modifier,
) {
    AppScaffold(
        modifier = modifier,
        title = stringResource(R.string.language),
        onBackClick = { onAction(LanguageAction.BackClick) },
    ) {
        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(items = AppLanguage.entries) {
                LanguageItem(
                    item = it,
                    selected = it == selectedItem,
                    onClick = { onAction(LanguageAction.LanguageChange(it)) },
                )
            }
        }
    }
}

@Composable
private fun LanguageItem(
    item: AppLanguage,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            Color.Transparent
        },
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = item.title ?: "System",
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Preview
@Composable
private fun LanguageScreenPreview() {
    Surface {
        LanguageScreenContent(
            onAction = { },
            selectedItem = AppLanguage.ENGLISH,
        )
    }
}
