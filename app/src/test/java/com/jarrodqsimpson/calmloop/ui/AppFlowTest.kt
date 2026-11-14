package com.jarrodqsimpson.calmloop.ui

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.jarrodqsimpson.calmloop.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AppFlowTest {
    @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun waitForTag(tag: String) {
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodes(androidx.compose.ui.test.hasTestTag(tag)).fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun `first launch shows disclaimer, then home, settings and privacy`() {
        waitForTag("welcome_screen")
        composeTestRule.onNodeWithTag("welcome_accept_button").performScrollTo().performClick()

        waitForTag("streak_card")
        composeTestRule.onNodeWithTag("settings_button").performClick()

        waitForTag("privacy_terms_card")
        composeTestRule.onNodeWithTag("privacy_terms_card").performScrollTo().performClick()

        waitForTag("about_screen")
    }
}
