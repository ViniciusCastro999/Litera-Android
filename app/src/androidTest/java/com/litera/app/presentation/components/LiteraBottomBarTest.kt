package com.litera.app.presentation.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.litera.app.core.common.FeatureFlags
import com.litera.app.core.navigation.Screen
import com.litera.app.core.theme.LiteraAppTheme
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class LiteraBottomBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsTheEnabledDestinations() {
        composeTestRule.setContent {
            LiteraAppTheme {
                LiteraBottomBar(currentRoute = Screen.Home.route, onNavigate = {})
            }
        }

        composeTestRule.onNodeWithText("Home").assertExists()
        composeTestRule.onNodeWithText("Explorar").assertExists()
        composeTestRule.onNodeWithText("Estante").assertExists()
        composeTestRule.onNodeWithText("Perfil").assertExists()
    }

    @Test
    fun hidesComunidadeWhileItsFeatureFlagIsOff() {
        // Documents current state rather than hardcoding it — this test
        // starts failing the moment someone flips the flag back on without
        // updating it, instead of silently asserting stale behavior.
        assertFalse(FeatureFlags.COMMUNITY_ENABLED)

        composeTestRule.setContent {
            LiteraAppTheme {
                LiteraBottomBar(currentRoute = Screen.Home.route, onNavigate = {})
            }
        }

        composeTestRule.onNodeWithText("Comunidade").assertDoesNotExist()
    }

    @Test
    fun tappingATabInvokesOnNavigateWithItsScreen() {
        var navigatedTo: Screen? = null
        composeTestRule.setContent {
            LiteraAppTheme {
                LiteraBottomBar(currentRoute = Screen.Home.route, onNavigate = { navigatedTo = it })
            }
        }

        composeTestRule.onNodeWithText("Estante").performClick()

        assert(navigatedTo?.route == Screen.Shelf.route)
    }
}
