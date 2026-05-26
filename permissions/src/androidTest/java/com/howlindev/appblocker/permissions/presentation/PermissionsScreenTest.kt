package com.howlindev.appblocker.permissions.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.howlindev.appblocker.permissions.domain.model.RequiredPermission
import org.junit.Rule
import org.junit.Test

class PermissionsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun permissionsScreen_showsMissingPermissions() {
        val missingPermissions = listOf(
            RequiredPermission.Overlay,
            RequiredPermission.UsageAccess
        )

        composeTestRule.setContent {
            PermissionsScreenContent(
                missingPermissions = missingPermissions,
                onAction = {}
            )
        }

        // Using substrings since titles might contain extra info
        composeTestRule.onNodeWithText("Overlay", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Usage", substring = true).assertIsDisplayed()
    }
}

