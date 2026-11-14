package com.jarrodqsimpson.calmloop.ui

import android.content.Context
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import com.jarrodqsimpson.calmloop.R
import com.jarrodqsimpson.calmloop.ui.onboarding.WelcomeScreen
import com.jarrodqsimpson.calmloop.ui.theme.CalmLoopTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class WelcomeScreenTest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun `app name is CalmLoop`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        assertEquals("CalmLoop", context.getString(R.string.app_name))
    }

    @Test
    fun `AI stays off unless the user opts in`() {
        var accepted: Boolean? = null
        composeTestRule.setContent {
            CalmLoopTheme { WelcomeScreen(aiAvailable = true, onAccept = { accepted = it }, onOpenPrivacy = {}) }
        }
        composeTestRule.onNodeWithTag("welcome_accept_button").performScrollTo().performClick()
        assertEquals(false, accepted)
    }

    @Test
    fun `opting in enables AI`() {
        var accepted: Boolean? = null
        composeTestRule.setContent {
            CalmLoopTheme { WelcomeScreen(aiAvailable = true, onAccept = { accepted = it }, onOpenPrivacy = {}) }
        }
        composeTestRule.onNodeWithTag("welcome_ai_toggle").performScrollTo().performClick()
        composeTestRule.onNodeWithTag("welcome_accept_button").performScrollTo().performClick()
        assertEquals(true, accepted)
    }

    @Test
    fun `AI toggle is disabled when the build has no AI relay`() {
        var accepted: Boolean? = null
        composeTestRule.setContent {
            CalmLoopTheme { WelcomeScreen(aiAvailable = false, onAccept = { accepted = it }, onOpenPrivacy = {}) }
        }
        composeTestRule.onNodeWithTag("welcome_ai_toggle").performScrollTo().assertIsNotEnabled()
        composeTestRule.onNodeWithTag("welcome_accept_button").performScrollTo().performClick()
        assertEquals(false, accepted)
    }
}
