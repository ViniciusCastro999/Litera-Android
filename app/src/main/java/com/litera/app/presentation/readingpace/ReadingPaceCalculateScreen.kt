package com.litera.app.presentation.readingpace

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.litera.app.presentation.components.LiteraPrimaryButton
import com.litera.app.presentation.components.LiteraTextField
import com.litera.app.presentation.components.icons.PhosphorIcons

@Composable
fun ReadingPaceCalculateScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    viewModel: ReadingPaceViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(PhosphorIcons.ArrowLeft, contentDescription = "Voltar")
            }
            Text(
                text = "Calcule o seu tempo",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Seu tempo de leitura de um minuto",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))

        LiteraTextField(
            value = uiState.totalPagesText,
            onValueChange = viewModel::updateTotalPages,
            label = "Total de páginas",
            keyboardType = KeyboardType.Number
        )
        Spacer(Modifier.height(12.dp))
        LiteraTextField(
            value = uiState.startPageText,
            onValueChange = viewModel::updateStartPage,
            label = "Página que iniciou",
            keyboardType = KeyboardType.Number
        )
        Spacer(Modifier.height(12.dp))
        LiteraTextField(
            value = uiState.endPageText,
            onValueChange = viewModel::updateEndPage,
            label = "Página que parou",
            keyboardType = KeyboardType.Number
        )
        Spacer(Modifier.height(12.dp))
        LiteraTextField(
            value = uiState.desiredDaysText,
            onValueChange = viewModel::updateDesiredDays,
            label = "Em quantos dias quer ler o livro",
            keyboardType = KeyboardType.Number
        )

        Spacer(Modifier.weight(1f))

        LiteraPrimaryButton(
            text = "Avançar",
            onClick = {
                viewModel.calculateResult()
                onNext()
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
