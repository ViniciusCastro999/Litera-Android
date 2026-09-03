package com.litera.app.presentation.components

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.litera.app.core.theme.LiteraAppTheme
import org.junit.Rule
import org.junit.Test

class LiteraButtonsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun primaryButtonInvokesOnClickWhenTapped() {
        var clicked = false
        composeTestRule.setContent {
            LiteraAppTheme {
                LiteraPrimaryButton(text = "Continuar", onClick = { clicked = true })
            }
        }

        composeTestRule.onNodeWithText("Continuar").performClick()

        assert(clicked)
    }

    @Test
    fun primaryButtonIsDisabledWhenEnabledIsFalse() {
        composeTestRule.setContent {
            LiteraAppTheme {
                LiteraPrimaryButton(text = "Continuar", onClick = {}, enabled = false)
            }
        }

        composeTestRule.onNodeWithText("Continuar").assertIsNotEnabled()
    }

    @Test
    fun primaryButtonHidesItsLabelWhileLoading() {
        composeTestRule.setContent {
            LiteraAppTheme {
                LiteraPrimaryButton(text = "Continuar", onClick = {}, isLoading = true)
            }
        }

        composeTestRule.onNodeWithText("Continuar").assertDoesNotExist()
    }

    @Test
    fun outlinedButtonInvokesOnClickWhenTapped() {
        var clicked = false
        composeTestRule.setContent {
            LiteraAppTheme {
                LiteraOutlinedButton(text = "Cancelar", onClick = { clicked = true })
            }
        }

        composeTestRule.onNodeWithText("Cancelar").performClick()

        assert(clicked)
    }

    @Test
    fun outlinedButtonRespectsEnabledFlag() {
        composeTestRule.setContent {
            LiteraAppTheme {
                LiteraOutlinedButton(text = "Cancelar", onClick = {}, enabled = true)
            }
        }

        composeTestRule.onNodeWithText("Cancelar").assertIsEnabled()
    }

    @Test
    fun textButtonInvokesOnClickWhenTapped() {
        var clicked = false
        composeTestRule.setContent {
            LiteraAppTheme {
                LiteraTextButton(text = "Pular", onClick = { clicked = true })
            }
        }

        composeTestRule.onNodeWithText("Pular").performClick()

        assert(clicked)
    }
}
