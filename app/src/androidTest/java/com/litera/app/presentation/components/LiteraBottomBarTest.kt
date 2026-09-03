package com.litera.app.presentation.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.litera.app.core.navigation.Screen
import com.litera.app.core.theme.LiteraAppTheme
import org.junit.Rule
import org.junit.Test

class LiteraBottomBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsAllFiveDestinations() {
        composeTestRule.setContent {
            LiteraAppTheme {
                LiteraBottomBar(currentRoute = Screen.Home.route, onNavigate = {})
            }
        }

        composeTestRule.onNodeWithText("Home").assertExists()
        composeTestRule.onNodeWithText("Explorar").assertExists()
        composeTestRule.onNodeWithText("Estante").assertExists()
        composeTestRule.onNodeWithText("Comunidade").assertExists()
        composeTestRule.onNodeWithText("Perfil").assertExists()
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
