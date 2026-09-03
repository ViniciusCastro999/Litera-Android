package com.litera.app.presentation.auth

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.analytics.FirebaseAnalytics
import com.litera.app.core.common.AnalyticsLogger
import com.litera.app.core.common.Resource
import com.litera.app.core.theme.LiteraAppTheme
import com.litera.app.domain.model.AuthUser
import com.litera.app.domain.usecase.ObserveCurrentUserUseCase
import com.litera.app.domain.usecase.SendPasswordResetUseCase
import com.litera.app.domain.usecase.SignInUseCase
import com.litera.app.domain.usecase.SignUpUseCase
import com.litera.app.fakes.FakeAuthRepository
import org.junit.Rule
import org.junit.Test

/**
 * Exercises the real AuthViewModel + use cases (constructed directly,
 * bypassing Hilt) against a FakeAuthRepository, so this test covers the
 * full Compose UI -> ViewModel -> use case chain, not just LoginScreen's
 * layout in isolation.
 */
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun viewModel(repository: FakeAuthRepository) = AuthViewModel(
        ObserveCurrentUserUseCase(repository),
        SignInUseCase(repository),
        SignUpUseCase(repository),
        SendPasswordResetUseCase(repository),
        AnalyticsLogger(FirebaseAnalytics.getInstance(InstrumentationRegistry.getInstrumentation().targetContext))
    )

    @Test
    fun enteringCredentialsAndTappingLoginNavigatesOnSuccess() {
        val repository = FakeAuthRepository(
            signInResult = Resource.Success(AuthUser(uid = "1", email = "reader@litera.com", displayName = "Reader"))
        )
        var loggedIn = false

        composeTestRule.setContent {
            LiteraAppTheme {
                LoginScreen(
                    onLoginSuccess = { loggedIn = true },
                    onNavigateToSignUp = {},
                    onNavigateToForgotPassword = {},
                    viewModel = viewModel(repository)
                )
            }
        }

        composeTestRule.onNodeWithText("nome@enderecodeemail.com").performTextInput("reader@litera.com")
        composeTestRule.onNodeWithText("Senha").performTextInput("123456")
        composeTestRule.onNodeWithText("Entrar").performClick()

        composeTestRule.waitForIdle()
        assert(loggedIn) { "onLoginSuccess should have been called after a successful login" }
    }

    @Test
    fun loginFailureShowsTheErrorMessageAndDoesNotNavigate() {
        val repository = FakeAuthRepository(
            signInResult = Resource.Error("Credenciais inválidas.")
        )
        var loggedIn = false

        composeTestRule.setContent {
            LiteraAppTheme {
                LoginScreen(
                    onLoginSuccess = { loggedIn = true },
                    onNavigateToSignUp = {},
                    onNavigateToForgotPassword = {},
                    viewModel = viewModel(repository)
                )
            }
        }

        composeTestRule.onNodeWithText("nome@enderecodeemail.com").performTextInput("reader@litera.com")
        composeTestRule.onNodeWithText("Senha").performTextInput("wrong-password")
        composeTestRule.onNodeWithText("Entrar").performClick()

        composeTestRule.onNodeWithText("Credenciais inválidas.").assertExists()
        assert(!loggedIn) { "onLoginSuccess must not be called when sign-in fails" }
    }

    @Test
    fun togglingThePasswordVisibilityIconFlipsItsDescription() {
        val repository = FakeAuthRepository()

        composeTestRule.setContent {
            LiteraAppTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToSignUp = {},
                    onNavigateToForgotPassword = {},
                    viewModel = viewModel(repository)
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Mostrar senha").assertExists()

        composeTestRule.onNodeWithContentDescription("Mostrar senha").performClick()

        composeTestRule.onNodeWithContentDescription("Ocultar senha").assertExists()
    }

    @Test
    fun tappingCreateAccountAndForgotPasswordInvokeTheirCallbacks() {
        val repository = FakeAuthRepository()
        var signUpTapped = false
        var forgotPasswordTapped = false

        composeTestRule.setContent {
            LiteraAppTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToSignUp = { signUpTapped = true },
                    onNavigateToForgotPassword = { forgotPasswordTapped = true },
                    viewModel = viewModel(repository)
                )
            }
        }

        composeTestRule.onNodeWithText("Criar conta").performClick()
        composeTestRule.onNodeWithText("Esqueci minha senha").performClick()

        assert(signUpTapped)
        assert(forgotPasswordTapped)
    }
}
