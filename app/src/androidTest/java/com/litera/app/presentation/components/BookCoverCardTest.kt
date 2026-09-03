package com.litera.app.presentation.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.litera.app.core.theme.LiteraAppTheme
import org.junit.Rule
import org.junit.Test

class BookCoverCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun rendersTitleAndAuthorAndReactsToClick() {
        var clicked = false
        composeTestRule.setContent {
            LiteraAppTheme {
                BookCoverCard(
                    title = "Dom Casmurro",
                    authorsLabel = "Machado de Assis",
                    thumbnailUrl = null,
                    onClick = { clicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Dom Casmurro").assertExists()
        composeTestRule.onNodeWithText("Machado de Assis").assertExists()

        composeTestRule.onNodeWithText("Dom Casmurro").performClick()

        assert(clicked)
    }

    @Test
    fun hidesTitleAndAuthorWhenShowTextIsFalse() {
        composeTestRule.setContent {
            LiteraAppTheme {
                BookCoverCard(
                    title = "Dom Casmurro",
                    authorsLabel = "Machado de Assis",
                    thumbnailUrl = null,
                    onClick = {},
                    showText = false
                )
            }
        }

        composeTestRule.onNodeWithText("Dom Casmurro").assertDoesNotExist()
    }
}
