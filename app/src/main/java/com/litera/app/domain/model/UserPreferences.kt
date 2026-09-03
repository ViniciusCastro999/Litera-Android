package com.litera.app.domain.model

data class UserPreferences(
    val selectedCategories: List<String> = emptyList(),
    val onboardingCompleted: Boolean = false,
    val quizCompleted: Boolean = false
)
