package com.litera.app.fakes

import com.litera.app.core.common.Resource
import com.litera.app.domain.model.AuthUser
import com.litera.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Hand-written test double used to drive real ViewModels (constructed
 * directly, without Hilt) in Compose UI tests — lets a screen test exercise
 * the actual ViewModel/use-case wiring while controlling exactly what the
 * "network"/"auth backend" returns.
 */
class FakeAuthRepository(
    private var signInResult: Resource<AuthUser> = Resource.Error("not configured"),
    private var signUpResult: Resource<AuthUser> = Resource.Error("not configured"),
    private var resetResult: Resource<Unit> = Resource.Error("not configured")
) : AuthRepository {

    private val _currentUser = MutableStateFlow<AuthUser?>(null)
    override val currentUser: StateFlow<AuthUser?> = _currentUser

    var signOutCallCount = 0
        private set

    fun setSignInResult(result: Resource<AuthUser>) {
        signInResult = result
    }

    override suspend fun signIn(email: String, password: String): Resource<AuthUser> = signInResult

    override suspend fun signUp(displayName: String, email: String, password: String): Resource<AuthUser> = signUpResult

    override suspend fun sendPasswordResetEmail(email: String): Resource<Unit> = resetResult

    override fun signOut() {
        signOutCallCount++
        _currentUser.value = null
    }
}
