package com.serhii.appblocker.profiles.presentation.list

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.serhii.appblocker.profiles.presentation.list.model.ProfileUi
import org.junit.Rule
import org.junit.Test

class ProfileListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun profileList_rendersProfiles() {
        val inactiveProfiles = listOf(
            ProfileUi(id = 1, name = "Work", blockedApps = emptyList()),
            ProfileUi(id = 2, name = "Study", blockedApps = emptyList())
        )

        composeTestRule.setContent {
            ProfileListScreenContent(
                inactiveProfiles = inactiveProfiles,
                activeProfile = null,
                formattedTimeRemaining = "",
                onAction = {}
            )
        }

        composeTestRule.onNodeWithText("Work").assertIsDisplayed()
        composeTestRule.onNodeWithText("Study").assertIsDisplayed()
    }

    @Test
    fun activeProfile_isDisplayed() {
        val activeProfile = ProfileUi(
            id = 1,
            name = "Reading",
            blockedApps = emptyList(),
            durationMillis = 1800000L // 30 mins
        )

        composeTestRule.setContent {
            ProfileListScreenContent(
                inactiveProfiles = emptyList(),
                activeProfile = activeProfile,
                formattedTimeRemaining = "00:30:00",
                onAction = {}
            )
        }

        composeTestRule.onNodeWithText("Reading").assertIsDisplayed()
        composeTestRule.onNodeWithText("00:30:00").assertIsDisplayed()
    }
}
