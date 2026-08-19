package com.howlindev.appblocker.presentation.root

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.howlindev.appblocker.core.ui.theme.AppBlockerTheme
import com.howlindev.appblocker.core.util.LocalAppIconProvider
import com.howlindev.appblocker.core.util.LocaleUtils
import com.howlindev.appblocker.util.AppIconLoader
import org.koin.androidx.compose.koinViewModel

@Composable
fun RootScreen(
    viewModel: RootViewModel = koinViewModel(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    LaunchedEffect(settings?.language) {
        settings?.language?.let { language ->
            val currentLocales = LocaleUtils.getLocale(context)
            if (currentLocales != language.tag) {
                LocaleUtils.applyLocale(context, language.tag)
            }
        }
    }

    AppBlockerTheme(
        themeMode = settings?.themeMode ?: com.howlindev.appblocker.core.domain.model.ThemeMode.SYSTEM,
        dynamicColor = settings?.dynamicColor ?: true,
    ) {
        CompositionLocalProvider(
            LocalAppIconProvider provides AppIconLoader(context = context),
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
            ) {
                content()
            }
        }
    }
}
