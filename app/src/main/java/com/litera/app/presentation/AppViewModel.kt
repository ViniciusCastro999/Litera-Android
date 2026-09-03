package com.litera.app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litera.app.domain.usecase.ObserveCurrentUserUseCase
import com.litera.app.domain.usecase.ObservePreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed class StartDestination {
    data object Loading : StartDestination()
    data object Onboarding : StartDestination()
    data object Auth : StartDestination()
    data object Quiz : StartDestination()
    data object Home : StartDestination()
}

/**
 * Decides which flow to show on cold start: first-run onboarding, the
 * auth screens, the preferences quiz, or straight to Home for a returning,
 * fully set-up user. Only used for the *initial* destination — sign-out
 * navigates explicitly from the Profile screen instead of reacting here.
 */
@HiltViewModel
class AppViewModel @Inject constructor(
    observePreferencesUseCase: ObservePreferencesUseCase,
    observeCurrentUserUseCase: ObserveCurrentUserUseCase
) : ViewModel() {

    val startDestination: StateFlow<StartDestination> = combine(
        observePreferencesUseCase(),
        observeCurrentUserUseCase()
    ) { preferences, user ->
        when {
            !preferences.onboardingCompleted -> StartDestination.Onboarding
            user == null -> StartDestination.Auth
            !preferences.quizCompleted -> StartDestination.Quiz
            else -> StartDestination.Home
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StartDestination.Loading
    )

    /**
     * Snapshot used right after a successful login/sign-up to decide whether
     * to route into the quiz or straight to Home, without waiting for
     * [startDestination] (which only drives the *initial* NavHost route).
     */
    val quizCompleted: StateFlow<Boolean> = observePreferencesUseCase()
        .map { it.quizCompleted }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
}
