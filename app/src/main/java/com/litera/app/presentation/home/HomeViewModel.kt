package com.litera.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litera.app.core.common.Resource
import com.litera.app.domain.model.Book
import com.litera.app.domain.model.ShelfBook
import com.litera.app.domain.usecase.GetBooksByCategoryUseCase
import com.litera.app.domain.usecase.GetRecommendedBooksUseCase
import com.litera.app.domain.usecase.ObservePreferencesUseCase
import com.litera.app.domain.usecase.ObserveShelfUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val continueReading: List<ShelfBook> = emptyList(),
    val recommended: List<Book> = emptyList(),
    val nationalHighlights: List<Book> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val observeShelfUseCase: ObserveShelfUseCase,
    private val observePreferencesUseCase: ObservePreferencesUseCase,
    private val getRecommendedBooksUseCase: GetRecommendedBooksUseCase,
    private val getBooksByCategoryUseCase: GetBooksByCategoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeShelfUseCase().collect { shelf ->
                val reading = shelf.filter { it.isCurrentlyReading }.sortedByDescending { it.updatedAt }
                _uiState.value = _uiState.value.copy(continueReading = reading)
            }
        }
        loadRecommendations()
    }

    private fun loadRecommendations() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val preferences = observePreferencesUseCase().first()

            val recommendedResult = getRecommendedBooksUseCase(preferences.selectedCategories)
            val nationalResult = getBooksByCategoryUseCase("Literatura Brasileira")

            val recommended = (recommendedResult as? Resource.Success)?.data.orEmpty()
            val national = (nationalResult as? Resource.Success)?.data.orEmpty()

            val error = (recommendedResult as? Resource.Error)?.message
                ?: (nationalResult as? Resource.Error)?.message

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = if (recommended.isEmpty() && national.isEmpty()) error else null,
                recommended = recommended,
                nationalHighlights = national
            )
        }
    }

    fun retry() = loadRecommendations()
}
