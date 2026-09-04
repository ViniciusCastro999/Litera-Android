package com.litera.app.presentation.shelf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.litera.app.domain.model.ShelfBook
import com.litera.app.presentation.components.BookCoverCard
import com.litera.app.presentation.components.BookCoverImage
import com.litera.app.presentation.components.EmptyState
import com.litera.app.presentation.components.LiteraPrimaryButton
import com.litera.app.presentation.components.LoadingState
import com.litera.app.presentation.components.ReadingProgressDialog
import com.litera.app.presentation.components.SectionHeader
import com.litera.app.presentation.components.icons.PhosphorIcons

@Composable
fun ShelfScreen(
    onBookClick: (String) -> Unit,
    viewModel: ShelfViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        LoadingState()
        return
    }

    val isEmpty = uiState.currentlyReading.isEmpty() && uiState.favorites.isEmpty() && uiState.read.isEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ShelfHeader(
            readingCount = uiState.currentlyReading.size,
            readCount = uiState.read.size,
            favoriteCount = uiState.favorites.size
        )

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
            if (isEmpty) {
                EmptyState(
                    title = "Sua estante está vazia",
                    subtitle = "Explore livros e adicione à sua leitura para vê-los aqui.",
                    icon = PhosphorIcons.Books
                )
            }

            if (uiState.currentlyReading.isNotEmpty()) {
                SectionHeader("Continue lendo", modifier = Modifier.padding(bottom = 12.dp))
                uiState.currentlyReading.forEach { item ->
                    ContinueReadingCard(
                        item = item,
                        onOpenBook = { onBookClick(item.volumeId) },
                        onContinue = { viewModel.startEditingProgress(item) }
                    )
                    Spacer(Modifier.height(12.dp))
                }
                Spacer(Modifier.height(12.dp))
            }

            if (uiState.favorites.isNotEmpty()) {
                SectionShelfRail("Favoritos", uiState.favorites, onBookClick)
                Spacer(Modifier.height(24.dp))
            }

            if (uiState.read.isNotEmpty()) {
                SectionShelfRail("Lidos", uiState.read, onBookClick)
            }
        }
    }

    uiState.editingItem?.let { item ->
        ReadingProgressDialog(
            initialCurrentPage = item.currentPage,
            initialTotalPages = item.totalPages,
            onDismiss = viewModel::dismissProgressDialog,
            onConfirm = { current, total -> viewModel.updateProgress(current, total) }
        )
    }
}

@Composable
private fun ShelfHeader(readingCount: Int, readCount: Int, favoriteCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
            .padding(24.dp)
    ) {
        Text(
            text = "Minha Estante",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondary
        )
        Text(
            text = "Tudo o que você está lendo, já leu e favoritou",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.8f)
        )

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ShelfStatColumn(value = readingCount, label = "Lendo")
            ShelfStatColumn(value = readCount, label = "Lidos")
            ShelfStatColumn(value = favoriteCount, label = "Favoritos")
        }
    }
}

@Composable
private fun ShelfStatColumn(value: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value.toString(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondary
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun ContinueReadingCard(item: ShelfBook, onOpenBook: () -> Unit, onContinue: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .clip(RoundedCornerShape(12.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BookCoverImage(
                thumbnailUrl = item.thumbnailUrl,
                modifier = Modifier
                    .width(64.dp)
                    .height(96.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(item.authorsLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { item.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(MaterialTheme.shapes.small),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LiteraPrimaryButton(text = "Continuar", onClick = onContinue, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun SectionShelfRail(title: String, books: List<ShelfBook>, onBookClick: (String) -> Unit) {
    SectionHeader(title, modifier = Modifier.padding(bottom = 12.dp))
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        items(books, key = { it.volumeId }) { book ->
            BookCoverCard(
                title = book.title,
                authorsLabel = book.authorsLabel,
                thumbnailUrl = book.thumbnailUrl,
                onClick = { onBookClick(book.volumeId) }
            )
        }
    }
}
