package com.litera.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litera.app.core.common.AnalyticsLogger
import com.litera.app.core.common.Resource
import com.litera.app.domain.model.AuthUser
import com.litera.app.domain.usecase.ObserveCurrentUserUseCase
import com.litera.app.domain.usecase.SendPasswordResetUseCase
import com.litera.app.domain.usecase.SignInUseCase
import com.litera.app.domain.usecase.SignInWithGoogleUseCase
import com.litera.app.domain.usecase.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthActionState {
    data object Idle : AuthActionState()
    data object Loading : AuthActionState()
    data class Success(val user: AuthUser) : AuthActionState()
    data class Error(val message: String) : AuthActionState()
}

sealed class PasswordResetState {
    data object Idle : PasswordResetState()
    data object Loading : PasswordResetState()
    data object Sent : PasswordResetState()
    data class Error(val message: String) : PasswordResetState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    observeCurrentUser: ObserveCurrentUserUseCase,
    private val signInUseCase: SignInUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val sendPasswordResetUseCase: SendPasswordResetUseCase,
    private val analyticsLogger: AnalyticsLogger
) : ViewModel() {

    val currentUser: StateFlow<AuthUser?> = observeCurrentUser()

    private val _authState = MutableStateFlow<AuthActionState>(AuthActionState.Idle)
    val authState: StateFlow<AuthActionState> = _authState.asStateFlow()

    private val _resetState = MutableStateFlow<PasswordResetState>(PasswordResetState.Idle)
    val resetState: StateFlow<PasswordResetState> = _resetState.asStateFlow()

    fun login(email: String, password: String) {
        _authState.value = AuthActionState.Loading
        viewModelScope.launch {
            when (val result = signInUseCase(email, password)) {
                is Resource.Success -> {
                    analyticsLogger.logLogin()
                    _authState.value = AuthActionState.Success(result.data)
                }
                is Resource.Error -> _authState.value = AuthActionState.Error(result.message)
                Resource.Loading -> Unit
            }
        }
    }

    fun signUp(displayName: String, email: String, password: String) {
        _authState.value = AuthActionState.Loading
        viewModelScope.launch {
            when (val result = signUpUseCase(displayName, email, password)) {
                is Resource.Success -> {
                    analyticsLogger.logSignUp()
                    _authState.value = AuthActionState.Success(result.data)
                }
                is Resource.Error -> _authState.value = AuthActionState.Error(result.message)
                Resource.Loading -> Unit
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        _authState.value = AuthActionState.Loading
        viewModelScope.launch {
            when (val result = signInWithGoogleUseCase(idToken)) {
                is Resource.Success -> {
                    analyticsLogger.logLogin(method = "google")
                    _authState.value = AuthActionState.Success(result.data)
                }
                is Resource.Error -> _authState.value = AuthActionState.Error(result.message)
                Resource.Loading -> Unit
            }
        }
    }

    fun sendPasswordReset(email: String) {
        _resetState.value = PasswordResetState.Loading
        viewModelScope.launch {
            when (val result = sendPasswordResetUseCase(email)) {
                is Resource.Success -> _resetState.value = PasswordResetState.Sent
                is Resource.Error -> _resetState.value = PasswordResetState.Error(result.message)
                Resource.Loading -> Unit
            }
        }
    }

    fun resetAuthState() {
        _authState.value = AuthActionState.Idle
    }

    fun resetPasswordResetState() {
        _resetState.value = PasswordResetState.Idle
    }
}
