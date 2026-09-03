package com.litera.app.domain.usecase

import com.litera.app.core.common.Resource
import com.litera.app.domain.model.AuthUser
import com.litera.app.domain.repository.AuthRepository
import com.litera.app.testutil.AndroidPatternsRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AuthUseCasesTest {

    @get:Rule
    val androidPatternsRule = AndroidPatternsRule()

    private val repository: AuthRepository = mockk()

    private lateinit var signIn: SignInUseCase
    private lateinit var signUp: SignUpUseCase
    private lateinit var sendPasswordReset: SendPasswordResetUseCase

    @Before
    fun setUp() {
        signIn = SignInUseCase(repository)
        signUp = SignUpUseCase(repository)
        sendPasswordReset = SendPasswordResetUseCase(repository)
    }

    @Test
    fun `signIn rejects blank email without calling repository`() = runTest {
        val result = signIn(email = "", password = "123456")

        assertTrue(result is Resource.Error)
        coVerify(exactly = 0) { repository.signIn(any(), any()) }
    }

    @Test
    fun `signIn rejects malformed email`() = runTest {
        val result = signIn(email = "not-an-email", password = "123456")

        assertTrue(result is Resource.Error)
        assertEquals("Digite um e-mail válido.", (result as Resource.Error).message)
    }

    @Test
    fun `signIn rejects blank password`() = runTest {
        val result = signIn(email = "reader@litera.com", password = "")

        assertTrue(result is Resource.Error)
        assertEquals("Digite sua senha.", (result as Resource.Error).message)
    }

    @Test
    fun `signIn trims email and delegates to repository when valid`() = runTest {
        val user = AuthUser(uid = "1", email = "reader@litera.com", displayName = "Reader")
        coEvery { repository.signIn("reader@litera.com", "123456") } returns Resource.Success(user)

        val result = signIn(email = "  reader@litera.com  ", password = "123456")

        assertEquals(Resource.Success(user), result)
        coVerify(exactly = 1) { repository.signIn("reader@litera.com", "123456") }
    }

    @Test
    fun `signUp rejects blank display name`() = runTest {
        val result = signUp(displayName = "", email = "reader@litera.com", password = "123456")

        assertTrue(result is Resource.Error)
        assertEquals("Digite um nome de usuário.", (result as Resource.Error).message)
    }

    @Test
    fun `signUp rejects malformed email`() = runTest {
        val result = signUp(displayName = "Reader", email = "invalid", password = "123456")

        assertTrue(result is Resource.Error)
        assertEquals("Digite um e-mail válido.", (result as Resource.Error).message)
    }

    @Test
    fun `signUp rejects password shorter than 6 chars`() = runTest {
        val result = signUp(displayName = "Reader", email = "reader@litera.com", password = "123")

        assertTrue(result is Resource.Error)
        assertEquals("A senha precisa ter pelo menos 6 caracteres.", (result as Resource.Error).message)
    }

    @Test
    fun `signUp trims name and email and delegates when valid`() = runTest {
        val user = AuthUser(uid = "1", email = "reader@litera.com", displayName = "Reader")
        coEvery { repository.signUp("Reader", "reader@litera.com", "123456") } returns Resource.Success(user)

        val result = signUp(displayName = "  Reader  ", email = "  reader@litera.com  ", password = "123456")

        assertEquals(Resource.Success(user), result)
        coVerify(exactly = 1) { repository.signUp("Reader", "reader@litera.com", "123456") }
    }

    @Test
    fun `sendPasswordReset rejects malformed email without calling repository`() = runTest {
        val result = sendPasswordReset("not-an-email")

        assertTrue(result is Resource.Error)
        coVerify(exactly = 0) { repository.sendPasswordResetEmail(any()) }
    }

    @Test
    fun `sendPasswordReset trims and delegates to repository when valid`() = runTest {
        coEvery { repository.sendPasswordResetEmail("reader@litera.com") } returns Resource.Success(Unit)

        val result = sendPasswordReset("  reader@litera.com  ")

        assertEquals(Resource.Success(Unit), result)
        coVerify(exactly = 1) { repository.sendPasswordResetEmail("reader@litera.com") }
    }
}
