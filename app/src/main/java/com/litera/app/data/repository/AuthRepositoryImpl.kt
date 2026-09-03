package com.litera.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import com.litera.app.core.common.Resource
import com.litera.app.data.remote.awaitResult
import com.litera.app.domain.model.AuthUser
import com.litera.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    private val _currentUser = MutableStateFlow(firebaseAuth.currentUser?.toDomain())
    override val currentUser: StateFlow<AuthUser?> = _currentUser

    init {
        firebaseAuth.addAuthStateListener { auth ->
            _currentUser.value = auth.currentUser?.toDomain()
        }
    }

    override suspend fun signIn(email: String, password: String): Resource<AuthUser> = try {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).awaitResult()
        val user = result.user?.toDomain()
        if (user != null) Resource.Success(user) else Resource.Error("Não foi possível entrar. Tente novamente.")
    } catch (e: Exception) {
        Resource.Error(e.toFriendlyMessage(), e)
    }

    override suspend fun signUp(displayName: String, email: String, password: String): Resource<AuthUser> = try {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).awaitResult()
        val firebaseUser = result.user
        firebaseUser?.updateProfile(
            UserProfileChangeRequest.Builder().setDisplayName(displayName).build()
        )?.awaitResult()
        val user = firebaseUser?.toDomain()?.copy(displayName = displayName)
        if (user != null) Resource.Success(user) else Resource.Error("Não foi possível criar a conta. Tente novamente.")
    } catch (e: Exception) {
        Resource.Error(e.toFriendlyMessage(), e)
    }

    override suspend fun sendPasswordResetEmail(email: String): Resource<Unit> = try {
        firebaseAuth.sendPasswordResetEmail(email).awaitResult()
        Resource.Success(Unit)
    } catch (e: Exception) {
        Resource.Error(e.toFriendlyMessage(), e)
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}

private fun com.google.firebase.auth.FirebaseUser.toDomain() = AuthUser(
    uid = uid,
    email = email,
    displayName = displayName
)

private fun Exception.toFriendlyMessage(): String = when (this) {
    is FirebaseAuthInvalidCredentialsException -> "E-mail ou senha incorretos."
    is FirebaseAuthInvalidUserException -> "Não encontramos uma conta com esse e-mail."
    is FirebaseAuthUserCollisionException -> "Já existe uma conta com esse e-mail."
    is FirebaseAuthWeakPasswordException -> "Escolha uma senha mais forte."
    else -> message ?: "Ocorreu um erro inesperado. Tente novamente."
}
