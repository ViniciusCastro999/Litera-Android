package com.litera.app.presentation.auth

import app.cash.turbine.test
import com.litera.app.core.common.AnalyticsLogger
import com.litera.app.core.common.Resource
import com.litera.app.domain.model.AuthUser
import com.litera.app.domain.usecase.ObserveCurrentUserUseCase
import com.litera.app.domain.usecase.SendPasswordResetUseCase
import com.litera.app.domain.usecase.SignInUseCase
import com.litera.app.domain.usecase.SignUpUseCase
import com.litera.app.testutil.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val observeCurrentUser: ObserveCurrentUserUseCase = mockk()
    private val signInUseCase: SignInUseCase = mockk()
    private val signUpUseCase: SignUpUseCase = mockk()
    private val sendPasswordResetUseCase: SendPasswordResetUseCase = mockk()
    private val analyticsLogger: AnalyticsLogger = mockk(relaxed = true)

    private lateinit var viewModel: AuthViewModel

    private val user = AuthUser(uid = "1", email = "reader@litera.com", displayName = "Reader")

    @Before
    fun setUp() {
        every { observeCurrentUser() } returns MutableStateFlow(null)
        viewModel = AuthViewModel(observeCurrentUser, signInUseCase, signUpUseCase, sendPasswordResetUseCase, analyticsLogger)
    }

    @Test
    fun `login success updates authState with the signed-in user`() = runTest {
        coEvery { signInUseCase("reader@litera.com", "123456") } returns Resource.Success(user)

        viewModel.authState.test {
            assertEquals(AuthActionState.Idle, awaitItem())

            viewModel.login("reader@litera.com", "123456")

            assertEquals(AuthActionState.Loading, awaitItem())
            assertEquals(AuthActionState.Success(user), awaitItem())
        }
        verify(exactly = 1) { analyticsLogger.logLogin() }
    }

    @Test
    fun `login failure updates authState with the error message and does not log an analytics event`() = runTest {
        coEvery { signInUseCase(any(), any()) } returns Resource.Error("Credenciais inválidas.")

        viewModel.authState.test {
            awaitItem() // Idle

            viewModel.login("reader@litera.com", "wrong")

            awaitItem() // Loading
            val error = awaitItem()
            assertTrue(error is AuthActionState.Error)
            assertEquals("Credenciais inválidas.", (error as AuthActionState.Error).message)
        }
        verify(exactly = 0) { analyticsLogger.logLogin() }
    }

    @Test
    fun `signUp success updates authState with the created user`() = runTest {
        coEvery { signUpUseCase("Reader", "reader@litera.com", "123456") } returns Resource.Success(user)

        viewModel.authState.test {
            awaitItem() // Idle

            viewModel.signUp("Reader", "reader@litera.com", "123456")

            awaitItem() // Loading
            assertEquals(AuthActionState.Success(user), awaitItem())
        }
        verify(exactly = 1) { analyticsLogger.logSignUp() }
    }

    @Test
    fun `sendPasswordReset success moves resetState to Sent`() = runTest {
        coEvery { sendPasswordResetUseCase("reader@litera.com") } returns Resource.Success(Unit)

        viewModel.resetState.test {
            assertEquals(PasswordResetState.Idle, awaitItem())

            viewModel.sendPasswordReset("reader@litera.com")

            assertEquals(PasswordResetState.Loading, awaitItem())
            assertEquals(PasswordResetState.Sent, awaitItem())
        }
    }

    @Test
    fun `resetAuthState returns authState to Idle`() = runTest {
        coEvery { signInUseCase(any(), any()) } returns Resource.Success(user)

        viewModel.authState.test {
            awaitItem() // Idle
            viewModel.login("reader@litera.com", "123456")
            awaitItem() // Loading
            awaitItem() // Success

            viewModel.resetAuthState()

            assertEquals(AuthActionState.Idle, awaitItem())
        }
    }
}
