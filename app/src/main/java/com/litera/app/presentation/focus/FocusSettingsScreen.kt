package com.litera.app.presentation.focus

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
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
import com.litera.app.presentation.components.LiteraPrimaryButton
import com.litera.app.presentation.components.LoadingState
import com.litera.app.presentation.components.icons.PhosphorIcons

private val durationPresets = listOf(15 * 60, 30 * 60, 60 * 60)

@Composable
fun FocusSettingsScreen(
    onBack: () -> Unit,
    viewModel: FocusSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        LoadingState()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(PhosphorIcons.ArrowLeft, contentDescription = "Voltar")
            }
            Text(
                text = "Tempo total de foco",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(24.dp))

        val totalHours = uiState.stats.totalFocusSeconds / 3600
        val totalMinutes = (uiState.stats.totalFocusSeconds % 3600) / 60
        val totalSecs = uiState.stats.totalFocusSeconds % 60

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.size(220.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(220.dp),
                    strokeWidth = 6.dp,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    trackColor = MaterialTheme.colorScheme.primaryContainer
                )
                Text(
                    text = "%02d:%02d:%02d".format(totalHours, totalMinutes, totalSecs),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text("Livros lidos: ${uiState.booksReadCount}", style = MaterialTheme.typography.bodyMedium)
            Text("🐱 ${uiState.stats.xp}xp", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(Modifier.height(32.dp))

        Text("Quando quero receber notificação:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            NotificationCheckbox(
                label = "Manhã",
                checked = uiState.settings.notifyMorning,
                onToggle = { viewModel.setNotificationPrefs(it, uiState.settings.notifyAfternoon, uiState.settings.notifyNight) }
            )
            NotificationCheckbox(
                label = "Tarde",
                checked = uiState.settings.notifyAfternoon,
                onToggle = { viewModel.setNotificationPrefs(uiState.settings.notifyMorning, it, uiState.settings.notifyNight) }
            )
            NotificationCheckbox(
                label = "Noite",
                checked = uiState.settings.notifyNight,
                onToggle = { viewModel.setNotificationPrefs(uiState.settings.notifyMorning, uiState.settings.notifyAfternoon, it) }
            )
        }

        Spacer(Modifier.height(24.dp))

        Text("Configurar tempo do timer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            durationPresets.forEach { seconds ->
                val selected = uiState.settings.selectedDurationSeconds == seconds
                val minutes = seconds / 60
                DurationChip(
                    label = "%02d:00".format(minutes),
                    selected = selected,
                    onClick = { viewModel.selectDuration(seconds) }
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        LiteraPrimaryButton(
            text = "Salvar",
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun NotificationCheckbox(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = onToggle)
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.width(8.dp))
    }
}

@Composable
private fun DurationChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}
