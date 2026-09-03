package com.litera.app.domain.usecase

import android.util.Patterns
import com.litera.app.core.common.Resource
import com.litera.app.domain.model.AuthUser
import com.litera.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ObserveCurrentUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): StateFlow<AuthUser?> = repository.currentUser
}

class SignInUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Resource<AuthUser> {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            return Resource.Error("Digite um e-mail válido.")
        }
        if (password.isBlank()) {
            return Resource.Error("Digite sua senha.")
        }
        return repository.signIn(trimmedEmail, password)
    }
}

class SignUpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(displayName: String, email: String, password: String): Resource<AuthUser> {
        val trimmedName = displayName.trim()
        if (trimmedName.isBlank()) {
            return Resource.Error("Digite um nome de usuário.")
        }
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            return Resource.Error("Digite um e-mail válido.")
        }
        if (password.length < 6) {
            return Resource.Error("A senha precisa ter pelo menos 6 caracteres.")
        }
        return repository.signUp(trimmedName, trimmedEmail, password)
    }
}

class SignInWithGoogleUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Resource<AuthUser> = repository.signInWithGoogle(idToken)
}

class SendPasswordResetUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String): Resource<Unit> {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            return Resource.Error("Digite um e-mail válido.")
        }
        return repository.sendPasswordResetEmail(trimmedEmail)
    }
}

class SignOutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke() = repository.signOut()
}
