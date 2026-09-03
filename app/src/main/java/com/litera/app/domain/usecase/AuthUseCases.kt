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
        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Resource.Error("Digite um e-mail válido.")
        }
        if (password.isBlank()) {
            return Resource.Error("Digite sua senha.")
        }
        return repository.signIn(email.trim(), password)
    }
}

class SignUpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(displayName: String, email: String, password: String): Resource<AuthUser> {
        if (displayName.isBlank()) {
            return Resource.Error("Digite um nome de usuário.")
        }
        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Resource.Error("Digite um e-mail válido.")
        }
        if (password.length < 6) {
            return Resource.Error("A senha precisa ter pelo menos 6 caracteres.")
        }
        return repository.signUp(displayName.trim(), email.trim(), password)
    }
}

class SendPasswordResetUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String): Resource<Unit> {
        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Resource.Error("Digite um e-mail válido.")
        }
        return repository.sendPasswordResetEmail(email.trim())
    }
}

class SignOutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke() = repository.signOut()
}
