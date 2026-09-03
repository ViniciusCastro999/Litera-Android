package com.litera.app.presentation.readingpace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.litera.app.presentation.components.LiteraOutlinedButton
import com.litera.app.presentation.components.LiteraPrimaryButton
import com.litera.app.presentation.components.icons.PhosphorIcons

@Composable
fun ReadingPaceResultScreen(
    onBack: () -> Unit,
    onFinish: () -> Unit,
    onContinueReading: () -> Unit,
    viewModel: ReadingPaceViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagesRead = ((uiState.endPageText.toIntOrNull() ?: 0) - (uiState.startPageText.toIntOrNull() ?: 0)).coerceAtLeast(0)

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
                text = "Resultado",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(1.dp))
        }

        Spacer(Modifier.height(24.dp))

        ResultRow(label = "Páginas lidas", value = "%02d".format(pagesRead))
        Spacer(Modifier.height(16.dp))
        ResultRow(label = "Tempo de leitura", value = formatElapsed(uiState.elapsedSeconds))
        Spacer(Modifier.height(16.dp))
        ResultRow(label = "Página / Hora", value = uiState.pagesPerHour.toString())

        Spacer(Modifier.height(32.dp))

        if (uiState.estimatedDays > 0) {
            Text(
                text = "Tempo necessário aproximadamente para concluir a leitura em ${uiState.estimatedDays} dias",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LiteraOutlinedButton(text = "Finalizar", onClick = onFinish, modifier = Modifier.weight(1f))
            LiteraPrimaryButton(text = "Continuar leitura", onClick = onContinueReading, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(value, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
        }
    }
}

private fun formatElapsed(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
