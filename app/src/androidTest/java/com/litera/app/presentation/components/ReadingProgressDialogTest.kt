package com.litera.app.presentation.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import com.litera.app.core.theme.LiteraAppTheme
import org.junit.Rule
import org.junit.Test

class ReadingProgressDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun confirmingSendsTheEditedPageNumbers() {
        var confirmed: Pair<Int, Int>? = null
        composeTestRule.setContent {
            LiteraAppTheme {
                ReadingProgressDialog(
                    initialCurrentPage = 50,
                    initialTotalPages = 200,
                    onDismiss = {},
                    onConfirm = { current, total -> confirmed = current to total }
                )
            }
        }

        composeTestRule.onNodeWithText("50").performTextReplacement("75")
        composeTestRule.onNodeWithText("Salvar").performClick()

        assert(confirmed == 75 to 200) { "expected (75, 200) but was $confirmed" }
    }

    @Test
    fun cancelInvokesOnDismiss() {
        var dismissed = false
        composeTestRule.setContent {
            LiteraAppTheme {
                ReadingProgressDialog(
                    initialCurrentPage = 0,
                    initialTotalPages = 0,
                    onDismiss = { dismissed = true },
                    onConfirm = { _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithText("Cancelar").performClick()

        assert(dismissed)
    }

    @Test
    fun nonDigitInputIsFilteredOut() {
        var confirmed: Pair<Int, Int>? = null
        composeTestRule.setContent {
            LiteraAppTheme {
                ReadingProgressDialog(
                    initialCurrentPage = 0,
                    initialTotalPages = 200,
                    onDismiss = {},
                    onConfirm = { current, total -> confirmed = current to total }
                )
            }
        }

        composeTestRule.onNodeWithText("Página atual").performTextInput("abc12")
        composeTestRule.onNodeWithText("Salvar").performClick()

        assert(confirmed?.first == 12) { "expected only digits to survive, got $confirmed" }
    }
}
