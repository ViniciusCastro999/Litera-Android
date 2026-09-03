package com.litera.app.presentation.progress

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.litera.app.presentation.components.LiteraOutlinedButton
import com.litera.app.presentation.components.LiteraPrimaryButton
import com.litera.app.presentation.components.LoadingState
import com.litera.app.presentation.components.icons.PhosphorIcons

@Composable
fun ReadingProgressScreen(
    onBack: () -> Unit,
    onNavigateToGoals: () -> Unit,
    viewModel: ReadingProgressViewModel = hiltViewModel()
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
    ) {
        ProgressHeaderCard(
            uiState = uiState,
            onBack = onBack,
            onNavigateToGoals = onNavigateToGoals
        )

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(24.dp))

            Text(
                text = "Minhas conquistas",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            AchievementsGrid(uiState.achievements)

            Spacer(Modifier.height(28.dp))

            Text(
                text = "Horas de leitura",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            FocusSessionsList(uiState.recentSessions)

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ProgressHeaderCard(
    uiState: ReadingProgressUiState,
    onBack: () -> Unit,
    onNavigateToGoals: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(PhosphorIcons.ArrowLeft, contentDescription = "Voltar")
            }
            Text(
                text = "Progresso de leitura",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Acompanhe diariamente seu progresso",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 48.dp)
        )

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Páginas lidas",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${uiState.pagesRead} / ${uiState.pagesTotal}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            ProgressRing(percent = uiState.progressPercent)
        }

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LiteraOutlinedButton(
                text = "Compartilhar",
                onClick = { /* Share intent not wired up for this screen yet. */ },
                modifier = Modifier.weight(1f)
            )
            LiteraPrimaryButton(
                text = "Ver metas",
                onClick = onNavigateToGoals,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun ProgressRing(percent: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(96.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.size(96.dp),
            color = MaterialTheme.colorScheme.surface,
            strokeWidth = 10.dp
        )
        CircularProgressIndicator(
            progress = { percent / 100f },
            modifier = Modifier.size(96.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 10.dp
        )
        Text(
            text = "$percent%",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

private fun iconFor(badge: AchievementBadge): ImageVector = when (badge) {
    AchievementBadge.FIRST_BOOK_READ -> PhosphorIcons.BookOpenText
    AchievementBadge.FIVE_BOOKS_READ -> PhosphorIcons.Books
    AchievementBadge.FOCUSED_60_MIN -> PhosphorIcons.PlayCircle
    AchievementBadge.TEN_FOCUS_SESSIONS -> PhosphorIcons.StarFill
    AchievementBadge.FIRST_GOAL_CREATED -> PhosphorIcons.PlusCircle
}

@Composable
private fun AchievementsGrid(achievements: List<AchievementUiModel>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.height(((achievements.size + 1) / 2 * 120).dp)
    ) {
        items(achievements) { achievement ->
            AchievementCard(achievement)
        }
    }
}

@Composable
private fun AchievementCard(achievement: AchievementUiModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .alpha(if (achievement.unlocked) 1f else 0.35f)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = iconFor(achievement.badge),
            contentDescription = achievement.badge.title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = achievement.badge.title,
            style = MaterialTheme.typography.labelMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun FocusSessionsList(sessions: List<FocusSessionUiModel>) {
    if (sessions.isEmpty()) {
        Text(
            text = "Nenhuma sessão de foco registrada ainda.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        sessions.forEach { session ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = PhosphorIcons.PlayCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(text = session.dateLabel, style = MaterialTheme.typography.bodyMedium)
                }
                Text(
                    text = session.minutesLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
