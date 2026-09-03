package com.litera.app.presentation.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litera.app.core.common.Constants
import com.litera.app.core.common.Resource
import com.litera.app.domain.usecase.SaveSelectedCategoriesUseCase
import com.litera.app.domain.usecase.SetOnboardingCompletedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizUiState(
    val availableCategories: List<String> = Constants.DEFAULT_CATEGORIES,
    val selectedCategories: Set<String> = emptySet(),
    val isSaving: Boolean = false,
    val errorMessage: String? = null
) {
    val canSave: Boolean get() = selectedCategories.size >= Constants.MIN_ONBOARDING_CATEGORIES
}

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val saveSelectedCategoriesUseCase: SaveSelectedCategoriesUseCase,
    private val setOnboardingCompletedUseCase: SetOnboardingCompletedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    fun toggleCategory(category: String) {
        _uiState.update { state ->
            val selected = state.selectedCategories.toMutableSet()
            if (!selected.add(category)) selected.remove(category)
            state.copy(selectedCategories = selected, errorMessage = null)
        }
    }

    fun save(onSaved: () -> Unit) {
        val state = _uiState.value
        _uiState.update { it.copy(isSaving = true, errorMessage = null) }
        viewModelScope.launch {
            when (val result = saveSelectedCategoriesUseCase(state.selectedCategories.toList())) {
                is Resource.Success -> {
                    setOnboardingCompletedUseCase()
                    _uiState.update { it.copy(isSaving = false) }
                    onSaved()
                }
                is Resource.Error -> _uiState.update { it.copy(isSaving = false, errorMessage = result.message) }
                Resource.Loading -> Unit
            }
        }
    }

    fun skip(onSaved: () -> Unit) {
        viewModelScope.launch {
            setOnboardingCompletedUseCase()
            onSaved()
        }
    }
}
