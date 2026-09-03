package com.litera.app.presentation.bookdetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.litera.app.presentation.components.icons.PhosphorIcons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.litera.app.presentation.components.BookCoverCard
import com.litera.app.presentation.components.BookCoverImage
import com.litera.app.presentation.components.ErrorState
import com.litera.app.presentation.components.LiteraOutlinedButton
import com.litera.app.presentation.components.LiteraPrimaryButton
import com.litera.app.presentation.components.LoadingState
import com.litera.app.presentation.components.ReadingProgressDialog

@Composable
fun BookDetailScreen(
    onBack: () -> Unit,
    onBookClick: (String) -> Unit,
    onAuthorClick: (String) -> Unit = {},
    onNotesClick: (String) -> Unit = {},
    onFocusModeClick: () -> Unit = {},
    viewModel: BookDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(PhosphorIcons.ArrowLeft, contentDescription = "Voltar")
            }
            Spacer(Modifier.weight(1f))
            uiState.book?.let { book ->
                IconButton(onClick = { onNotesClick(book.volumeId) }) {
                    Icon(PhosphorIcons.BookOpenText, contentDescription = "Anotações")
                }
            }
        }

        when {
            uiState.isLoading -> LoadingState()
            uiState.errorMessage != null -> ErrorState(uiState.errorMessage.orEmpty())
            uiState.book != null -> BookDetailContent(uiState, viewModel, onBookClick, onAuthorClick, onFocusModeClick)
        }
    }

    if (uiState.showProgressDialog) {
        val shelf = uiState.shelfInfo
        val book = uiState.book
        ReadingProgressDialog(
            initialCurrentPage = shelf?.currentPage ?: 0,
            initialTotalPages = shelf?.totalPages ?: book?.pageCount ?: 0,
            onDismiss = { viewModel.showProgressDialog(false) },
            onConfirm = { current, total -> viewModel.updateProgress(current, total) }
        )
    }
}

@Composable
private fun BookDetailContent(
    uiState: BookDetailUiState,
    viewModel: BookDetailViewModel,
    onBookClick: (String) -> Unit,
    onAuthorClick: (String) -> Unit,
    onFocusModeClick: () -> Unit
) {
    val book = uiState.book ?: return
    val shelfInfo = uiState.shelfInfo

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            BookCoverImage(
                thumbnailUrl = book.thumbnailUrl,
                modifier = Modifier
                    .width(160.dp)
                    .height(240.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = book.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Text(
            text = book.authorsLabel,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .let { base ->
                    val author = book.authors.firstOrNull()
                    if (author != null) {
                        base.clickable { onAuthorClick(author) }
                    } else {
                        base
                    }
                },
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        if (book.averageRating != null) {
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(PhosphorIcons.StarFill, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.height(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("${book.averageRating} (${book.ratingsCount})", style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LiteraPrimaryButton(
                text = when {
                    shelfInfo?.isRead == true -> "Lido ✓"
                    shelfInfo?.isCurrentlyReading == true -> "Atualizar leitura"
                    else -> "Ler"
                },
                onClick = {
                    if (shelfInfo?.isCurrentlyReading == true) {
                        viewModel.showProgressDialog(true)
                    } else if (shelfInfo?.isRead != true) {
                        viewModel.startReading()
                    }
                },
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = viewModel::toggleFavorite) {
                Icon(
                    imageVector = if (shelfInfo?.isFavorite == true) PhosphorIcons.HeartFill else PhosphorIcons.Heart,
                    contentDescription = "Favoritar",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (shelfInfo?.isCurrentlyReading == true) {
            Spacer(Modifier.height(12.dp))
            LiteraOutlinedButton(
                text = "Modo foco",
                onClick = onFocusModeClick,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(24.dp))

        Text("Sinopse", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(book.description, style = MaterialTheme.typography.bodyMedium)

        if (book.categories.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = book.categories.joinToString(" • "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (uiState.relatedByAuthor.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Mais de ${book.authorsLabel}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.relatedByAuthor, key = { it.volumeId }) { related ->
                    BookCoverCard(
                        title = related.title,
                        authorsLabel = related.authorsLabel,
                        thumbnailUrl = related.thumbnailUrl,
                        onClick = { onBookClick(related.volumeId) }
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}
