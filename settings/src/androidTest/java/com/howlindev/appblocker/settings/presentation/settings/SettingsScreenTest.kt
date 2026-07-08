package com.howlindev.appblocker.settings.presentation.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.howlindev.appblocker.core.domain.model.ThemeMode
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun settingsScreen_rendersCorrectly() {
        composeTestRule.setContent {
            SettingsScreenContent(
                currentThemeMode = ThemeMode.SYSTEM,
                isDynamicColorEnabled = false,
                onAction = {}
            )
        }

        composeTestRule.onNodeWithText("Theme", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Language", substring = true).assertIsDisplayed()
    }
}

