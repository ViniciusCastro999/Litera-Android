package com.litera.app.presentation.shelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litera.app.domain.model.ShelfBook
import com.litera.app.domain.usecase.ObserveShelfUseCase
import com.litera.app.domain.usecase.RemoveFromShelfUseCase
import com.litera.app.domain.usecase.UpdateReadingProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShelfUiState(
    val isLoading: Boolean = true,
    val currentlyReading: List<ShelfBook> = emptyList(),
    val favorites: List<ShelfBook> = emptyList(),
    val read: List<ShelfBook> = emptyList(),
    val editingItem: ShelfBook? = null
)

@HiltViewModel
class ShelfViewModel @Inject constructor(
    private val observeShelfUseCase: ObserveShelfUseCase,
    private val updateReadingProgressUseCase: UpdateReadingProgressUseCase,
    private val removeFromShelfUseCase: RemoveFromShelfUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShelfUiState())
    val uiState: StateFlow<ShelfUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeShelfUseCase().collect { shelf ->
                _uiState.value = ShelfUiState(
                    isLoading = false,
                    currentlyReading = shelf.filter { it.isCurrentlyReading }.sortedByDescending { it.updatedAt },
                    favorites = shelf.filter { it.isFavorite },
                    read = shelf.filter { it.isRead }.sortedByDescending { it.updatedAt },
                    editingItem = _uiState.value.editingItem
                )
            }
        }
    }

    fun startEditingProgress(item: ShelfBook) {
        _uiState.value = _uiState.value.copy(editingItem = item)
    }

    fun dismissProgressDialog() {
        _uiState.value = _uiState.value.copy(editingItem = null)
    }

    fun updateProgress(currentPage: Int, totalPages: Int) {
        val item = _uiState.value.editingItem ?: return
        viewModelScope.launch {
            updateReadingProgressUseCase(item.volumeId, currentPage, totalPages)
            dismissProgressDialog()
        }
    }

    fun removeFromShelf(volumeId: String) {
        viewModelScope.launch { removeFromShelfUseCase(volumeId) }
    }
}
