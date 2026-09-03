package com.litera.app.presentation.bookdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litera.app.core.common.Resource
import com.litera.app.core.navigation.Screen
import com.litera.app.domain.model.Book
import com.litera.app.domain.model.ShelfBook
import com.litera.app.domain.usecase.GetBookDetailUseCase
import com.litera.app.domain.usecase.GetBooksByAuthorUseCase
import com.litera.app.domain.usecase.MarkAsReadUseCase
import com.litera.app.domain.usecase.ObserveShelfItemUseCase
import com.litera.app.domain.usecase.StartReadingUseCase
import com.litera.app.domain.usecase.ToggleFavoriteUseCase
import com.litera.app.domain.usecase.UpdateReadingProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookDetailUiState(
    val isLoading: Boolean = true,
    val book: Book? = null,
    val shelfInfo: ShelfBook? = null,
    val relatedByAuthor: List<Book> = emptyList(),
    val errorMessage: String? = null,
    val showProgressDialog: Boolean = false
)

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getBookDetailUseCase: GetBookDetailUseCase,
    private val getBooksByAuthorUseCase: GetBooksByAuthorUseCase,
    private val observeShelfItemUseCase: ObserveShelfItemUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val startReadingUseCase: StartReadingUseCase,
    private val updateReadingProgressUseCase: UpdateReadingProgressUseCase,
    private val markAsReadUseCase: MarkAsReadUseCase
) : ViewModel() {

    private val volumeId: String = java.net.URLDecoder.decode(
        checkNotNull(savedStateHandle[Screen.BookDetail.ARG_VOLUME_ID]),
        "UTF-8"
    )

    private val _uiState = MutableStateFlow(BookDetailUiState())
    val uiState: StateFlow<BookDetailUiState> = _uiState.asStateFlow()

    init {
        loadBook()
        observeShelfState()
    }

    private fun loadBook() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = getBookDetailUseCase(volumeId)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, book = result.data)
                    loadRelatedByAuthor(result.data)
                }
                is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
                Resource.Loading -> Unit
            }
        }
    }

    private fun loadRelatedByAuthor(book: Book) {
        val author = book.authors.firstOrNull() ?: return
        viewModelScope.launch {
            val result = getBooksByAuthorUseCase(author, excludeVolumeId = book.volumeId)
            if (result is Resource.Success) {
                _uiState.value = _uiState.value.copy(relatedByAuthor = result.data)
            }
        }
    }

    private fun observeShelfState() {
        viewModelScope.launch {
            observeShelfItemUseCase(volumeId).collect { shelfBook ->
                _uiState.value = _uiState.value.copy(shelfInfo = shelfBook)
            }
        }
    }

    fun toggleFavorite() {
        val book = _uiState.value.book ?: return
        viewModelScope.launch { toggleFavoriteUseCase(book) }
    }

    fun startReading() {
        val book = _uiState.value.book ?: return
        viewModelScope.launch { startReadingUseCase(book, book.pageCount) }
    }

    fun markAsRead() {
        viewModelScope.launch { markAsReadUseCase(volumeId) }
    }

    fun showProgressDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showProgressDialog = show)
    }

    fun updateProgress(currentPage: Int, totalPages: Int) {
        viewModelScope.launch {
            updateReadingProgressUseCase(volumeId, currentPage, totalPages)
            showProgressDialog(false)
        }
    }
}
