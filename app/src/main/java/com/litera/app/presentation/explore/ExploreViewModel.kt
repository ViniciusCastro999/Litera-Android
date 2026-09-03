package com.litera.app.presentation.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litera.app.core.common.Constants
import com.litera.app.core.common.Resource
import com.litera.app.domain.model.Book
import com.litera.app.domain.usecase.GetRecommendedBooksUseCase
import com.litera.app.domain.usecase.ObservePreferencesUseCase
import com.litera.app.domain.usecase.SearchBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExploreUiState(
    val query: String = "",
    val isSearching: Boolean = false,
    val results: List<Book> = emptyList(),
    val errorMessage: String? = null,
    val categories: List<String> = Constants.DEFAULT_CATEGORIES,
    val suggested: List<Book> = emptyList()
)

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val searchBooksUseCase: SearchBooksUseCase,
    private val getRecommendedBooksUseCase: GetRecommendedBooksUseCase,
    private val observePreferencesUseCase: ObservePreferencesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadSuggested()
    }

    private fun loadSuggested() {
        viewModelScope.launch {
            val preferences = observePreferencesUseCase().first()
            val categories = preferences.selectedCategories.ifEmpty { Constants.DEFAULT_CATEGORIES.take(3) }
            val result = getRecommendedBooksUseCase(categories)
            if (result is Resource.Success) {
                _uiState.update { it.copy(suggested = result.data) }
            }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
        searchJob?.cancel()
        if (query.isBlank()) {
            _uiState.update { it.copy(results = emptyList(), isSearching = false, errorMessage = null) }
            return
        }
        searchJob = viewModelScope.launch {
            delay(400) // debounce
            _uiState.update { it.copy(isSearching = true, errorMessage = null) }
            when (val result = searchBooksUseCase(query)) {
                is Resource.Success -> _uiState.update {
                    it.copy(isSearching = false, results = result.data)
                }
                is Resource.Error -> _uiState.update {
                    it.copy(isSearching = false, errorMessage = result.message, results = emptyList())
                }
                Resource.Loading -> Unit
            }
        }
    }
}
