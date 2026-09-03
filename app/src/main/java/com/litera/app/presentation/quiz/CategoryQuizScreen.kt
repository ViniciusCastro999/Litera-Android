package com.litera.app.presentation.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.litera.app.presentation.components.CategoryChip
import com.litera.app.presentation.components.LiteraOutlinedButton
import com.litera.app.presentation.components.LiteraPrimaryButton

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryQuizScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text(
            text = "Selecione três ou mais categorias",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(20.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            uiState.availableCategories.forEach { category ->
                CategoryChip(
                    label = category,
                    selected = category in uiState.selectedCategories,
                    onClick = { viewModel.toggleCategory(category) }
                )
            }
        }

        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(8.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LiteraOutlinedButton(text = "Voltar", onClick = onBack, modifier = Modifier.weight(1f))
            LiteraPrimaryButton(
                text = "Salvar",
                onClick = { viewModel.save(onSaved) },
                enabled = uiState.canSave,
                isLoading = uiState.isSaving,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
