package com.litera.app.domain.repository

import com.litera.app.core.common.Resource
import com.litera.app.domain.model.AuthUser
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentUser: StateFlow<AuthUser?>

    suspend fun signIn(email: String, password: String): Resource<AuthUser>
    suspend fun signUp(displayName: String, email: String, password: String): Resource<AuthUser>
    suspend fun sendPasswordResetEmail(email: String): Resource<Unit>
    fun signOut()
}
