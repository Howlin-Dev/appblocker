package com.serhii.appblocker.settings.presentation.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.serhii.appblocker.core.domain.model.ThemeMode
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
                onAction = {}
            )
        }

        // We check for some key texts. 
        // Note: These might need to be resource-based if we want to support multiple locales in tests, 
        // but usually, we test in a fixed locale (like English).
        composeTestRule.onNodeWithText("Theme", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Language", substring = true).assertIsDisplayed()
    }
}
