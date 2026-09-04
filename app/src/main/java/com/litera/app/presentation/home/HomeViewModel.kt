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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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
    val nationalHighlights: List<Book> = emptyList(),
    val brazilianNovels: List<Book> = emptyList(),
    val brazilianShortStories: List<Book> = emptyList(),
    val brazilianPoetry: List<Book> = emptyList(),
    // Rolled once per Home visit (not per recomposition) so the hero
    // banner doesn't flicker between the quote and an ad while scrolling.
    val showAdInHero: Boolean = true
)

/** Ad most of the time, quote occasionally — see HeroBanner in HomeScreen.kt. */
private const val AD_PROBABILITY = 0.8

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val observeShelfUseCase: ObserveShelfUseCase,
    private val observePreferencesUseCase: ObservePreferencesUseCase,
    private val getRecommendedBooksUseCase: GetRecommendedBooksUseCase,
    private val getBooksByCategoryUseCase: GetBooksByCategoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(showAdInHero = kotlin.random.Random.nextDouble() < AD_PROBABILITY))
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

            // All five rails are independent reads, so they're fetched
            // concurrently instead of one-by-one — five sequential Google
            // Books calls would make the first paint noticeably slower.
            val (recommendedResult, nationalResult, novelsResult, shortStoriesResult, poetryResult) = coroutineScope {
                val recommendedDeferred = async { getRecommendedBooksUseCase(preferences.selectedCategories) }
                val nationalDeferred = async { getBooksByCategoryUseCase("Literatura Brasileira") }
                val novelsDeferred = async { getBooksByCategoryUseCase("Romance Brasileiro") }
                val shortStoriesDeferred = async { getBooksByCategoryUseCase("Contos Brasileiros") }
                val poetryDeferred = async { getBooksByCategoryUseCase("Poesia Brasileira") }
                listOf(recommendedDeferred, nationalDeferred, novelsDeferred, shortStoriesDeferred, poetryDeferred).awaitAll()
            }

            val recommended = (recommendedResult as? Resource.Success)?.data.orEmpty()
            val national = (nationalResult as? Resource.Success)?.data.orEmpty()
            val novels = (novelsResult as? Resource.Success)?.data.orEmpty()
            val shortStories = (shortStoriesResult as? Resource.Success)?.data.orEmpty()
            val poetry = (poetryResult as? Resource.Success)?.data.orEmpty()

            val allEmpty = recommended.isEmpty() && national.isEmpty() && novels.isEmpty() &&
                shortStories.isEmpty() && poetry.isEmpty()
            val error = (recommendedResult as? Resource.Error)?.message
                ?: (nationalResult as? Resource.Error)?.message

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = if (allEmpty) error else null,
                recommended = recommended,
                nationalHighlights = national,
                brazilianNovels = novels,
                brazilianShortStories = shortStories,
                brazilianPoetry = poetry
            )
        }
    }

    fun retry() = loadRecommendations()
}
