package com.litera.app.presentation.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.litera.app.core.theme.LiteraAppTheme
import org.junit.Rule
import org.junit.Test

class ScreenStatesTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun errorStateShowsTheMessage() {
        composeTestRule.setContent {
            LiteraAppTheme {
                ErrorState(message = "Sem conexão com a internet.")
            }
        }

        composeTestRule.onNodeWithText("Sem conexão com a internet.").assertExists()
    }

    @Test
    fun errorStateHidesRetryButtonWhenNoCallbackIsGiven() {
        composeTestRule.setContent {
            LiteraAppTheme {
                ErrorState(message = "Erro", onRetry = null)
            }
        }

        composeTestRule.onNodeWithText("Tentar novamente").assertDoesNotExist()
    }

    @Test
    fun errorStateRetryButtonInvokesTheCallback() {
        var retried = false
        composeTestRule.setContent {
            LiteraAppTheme {
                ErrorState(message = "Erro", onRetry = { retried = true })
            }
        }

        composeTestRule.onNodeWithText("Tentar novamente").performClick()

        assert(retried)
    }

    @Test
    fun sectionHeaderShowsTitleAndAction() {
        composeTestRule.setContent {
            LiteraAppTheme {
                SectionHeader(title = "Recomendados", action = { androidx.compose.material3.Text("Ver todos") })
            }
        }

        composeTestRule.onNodeWithText("Recomendados").assertExists()
        composeTestRule.onNodeWithText("Ver todos").assertExists()
    }
}
