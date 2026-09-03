package com.litera.app.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

private const val PREFS_NAME = "litera_onboarding"
private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"

/**
 * Whether this device has seen the onboarding carousel — shown before any
 * sign-in happens, so unlike quiz categories it can't live in the per-user
 * Firestore preferences doc (there's no uid yet at that point). Plain
 * SharedPreferences is enough for a single device-level boolean.
 */
@Singleton
class OnboardingPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _completed = MutableStateFlow(prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false))
    val completed: StateFlow<Boolean> = _completed

    fun setCompleted(completed: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
        _completed.value = completed
    }
}
