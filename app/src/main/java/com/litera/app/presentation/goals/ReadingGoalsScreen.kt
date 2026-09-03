package com.litera.app.presentation.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.litera.app.domain.model.ReadingGoal
import com.litera.app.presentation.components.LiteraOutlinedButton
import com.litera.app.presentation.components.LiteraPrimaryButton
import com.litera.app.presentation.components.LoadingState
import com.litera.app.presentation.components.icons.PhosphorIcons

@Composable
fun ReadingGoalsScreen(
    onBack: () -> Unit,
    viewModel: ReadingGoalsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(PhosphorIcons.ArrowLeft, contentDescription = "Voltar")
            }
            Text(
                text = "Minhas metas de leitura",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (uiState.isLoading) {
            LoadingState()
        } else {
            ReadingGoalsContent(
                goals = uiState.goals,
                onGoalClick = { viewModel.openEditDialog(it) },
                onEditClick = {
                    val mostRecent = uiState.goals.maxByOrNull { it.createdAt }
                    if (mostRecent != null) viewModel.openEditDialog(mostRecent)
                },
                onCreateClick = { viewModel.openCreateDialog() }
            )
        }
    }

    if (uiState.showDialog) {
        ReadingGoalDialog(
            editingGoal = uiState.editingGoal,
            onDismiss = { viewModel.dismissDialog() },
            onConfirm = { label, type, targetValue -> viewModel.saveGoal(label, type, targetValue) }
        )
    }
}

@Composable
private fun ReadingGoalsContent(
    goals: List<ReadingGoal>,
    onGoalClick: (ReadingGoal) -> Unit,
    onEditClick: () -> Unit,
    onCreateClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Defina metas e acompanhe seu progresso",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            goals.forEach { goal ->
                ReadingGoalRow(goal = goal, onClick = { onGoalClick(goal) })
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LiteraOutlinedButton(
                text = "Editar meta",
                onClick = onEditClick,
                modifier = Modifier.weight(1f)
            )
            LiteraPrimaryButton(
                text = "Criar meta",
                onClick = onCreateClick,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun ReadingGoalRow(goal: ReadingGoal, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = goal.label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
