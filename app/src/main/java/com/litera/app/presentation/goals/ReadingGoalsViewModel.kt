package com.litera.app.presentation.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litera.app.domain.model.ReadingGoal
import com.litera.app.domain.model.ReadingGoalType
import com.litera.app.domain.usecase.DeleteReadingGoalUseCase
import com.litera.app.domain.usecase.ObserveReadingGoalsUseCase
import com.litera.app.domain.usecase.SaveReadingGoalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReadingGoalsUiState(
    val isLoading: Boolean = true,
    val goals: List<ReadingGoal> = emptyList(),
    val editingGoal: ReadingGoal? = null,
    val showDialog: Boolean = false
)

@HiltViewModel
class ReadingGoalsViewModel @Inject constructor(
    private val observeReadingGoalsUseCase: ObserveReadingGoalsUseCase,
    private val saveReadingGoalUseCase: SaveReadingGoalUseCase,
    private val deleteReadingGoalUseCase: DeleteReadingGoalUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReadingGoalsUiState())
    val uiState: StateFlow<ReadingGoalsUiState> = _uiState.asStateFlow()

    init {
        observeGoals()
    }

    private fun observeGoals() {
        viewModelScope.launch {
            observeReadingGoalsUseCase().collect { goals ->
                _uiState.value = _uiState.value.copy(isLoading = false, goals = goals)
            }
        }
    }

    fun openCreateDialog() {
        _uiState.value = _uiState.value.copy(editingGoal = null, showDialog = true)
    }

    fun openEditDialog(goal: ReadingGoal) {
        _uiState.value = _uiState.value.copy(editingGoal = goal, showDialog = true)
    }

    fun dismissDialog() {
        _uiState.value = _uiState.value.copy(showDialog = false, editingGoal = null)
    }

    fun saveGoal(label: String, type: ReadingGoalType, targetValue: Int) {
        viewModelScope.launch {
            val editingGoal = _uiState.value.editingGoal
            val goal = ReadingGoal(
                id = editingGoal?.id ?: 0L,
                label = label,
                type = type,
                targetValue = targetValue,
                progressValue = editingGoal?.progressValue ?: 0,
                createdAt = editingGoal?.createdAt ?: System.currentTimeMillis()
            )
            saveReadingGoalUseCase(goal)
            dismissDialog()
        }
    }

    fun deleteGoal(id: Long) {
        viewModelScope.launch { deleteReadingGoalUseCase(id) }
    }
}
