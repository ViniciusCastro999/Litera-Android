package com.litera.app.presentation.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.litera.app.core.theme.LiteraAppTheme
import org.junit.Rule
import org.junit.Test

class CategoryChipTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun rendersTheGivenLabel() {
        composeTestRule.setContent {
            LiteraAppTheme {
                CategoryChip(label = "Fantasia", selected = false, onClick = {})
            }
        }

        composeTestRule.onNodeWithText("Fantasia").assertExists()
    }

    @Test
    fun invokesOnClickWhenTapped() {
        var clicked = false
        composeTestRule.setContent {
            LiteraAppTheme {
                CategoryChip(label = "Fantasia", selected = false, onClick = { clicked = true })
            }
        }

        composeTestRule.onNodeWithText("Fantasia").performClick()

        assert(clicked)
    }
}
