package com.litera.app.presentation.shelf

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
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
import com.litera.app.domain.model.ShelfBook
import com.litera.app.presentation.components.BookCoverCard
import com.litera.app.presentation.components.BookCoverImage
import com.litera.app.presentation.components.LiteraPrimaryButton
import com.litera.app.presentation.components.LoadingState
import com.litera.app.presentation.components.ReadingProgressDialog

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Minha Estante", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        if (uiState.currentlyReading.isEmpty() && uiState.favorites.isEmpty() && uiState.read.isEmpty()) {
            Text(
                "Sua estante está vazia. Explore livros e adicione à sua leitura!",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (uiState.currentlyReading.isNotEmpty()) {
            Text("Continue lendo", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            uiState.currentlyReading.forEach { item ->
                ContinueReadingRow(
                    item = item,
                    onOpenBook = { onBookClick(item.volumeId) },
                    onContinue = { viewModel.startEditingProgress(item) }
                )
                Spacer(Modifier.height(12.dp))
            }
            Spacer(Modifier.height(16.dp))
        }

        if (uiState.favorites.isNotEmpty()) {
            SectionShelfRail("Favoritos", uiState.favorites, onBookClick)
            Spacer(Modifier.height(16.dp))
        }

        if (uiState.read.isNotEmpty()) {
            SectionShelfRail("Lidos", uiState.read, onBookClick)
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
private fun ContinueReadingRow(item: ShelfBook, onOpenBook: () -> Unit, onContinue: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        BookCoverImage(
            thumbnailUrl = item.thumbnailUrl,
            modifier = Modifier
                .width(64.dp)
                .height(96.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(item.authorsLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { item.progress },
                modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.small)
            )
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LiteraPrimaryButton(text = "Continuar", onClick = onContinue, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SectionShelfRail(title: String, books: List<ShelfBook>, onBookClick: (String) -> Unit) {
    Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))
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
