package com.litera.app.presentation.home

import androidx.compose.foundation.background
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.litera.app.domain.model.Book
import com.litera.app.domain.model.ShelfBook
import com.litera.app.presentation.components.BannerAdView
import com.litera.app.presentation.components.BookCoverCard
import com.litera.app.presentation.components.BookCoverImage
import com.litera.app.presentation.components.ErrorState
import com.litera.app.presentation.components.LiteraPrimaryButton
import com.litera.app.presentation.components.LoadingState

@Composable
fun HomeScreen(
    onBookClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        HeroBanner(
            showAd = uiState.showAdInHero,
            onReadClick = {
                val target = uiState.continueReading.firstOrNull()?.volumeId
                    ?: uiState.recommended.firstOrNull()?.volumeId
                target?.let(onBookClick)
            }
        )

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            if (uiState.continueReading.isNotEmpty()) {
                SectionTitle("Continuar sua última leitura")
                uiState.continueReading.take(1).forEach { shelfBook ->
                    ContinueReadingCard(shelfBook, onClick = { onBookClick(shelfBook.volumeId) })
                }
                Spacer(Modifier.height(8.dp))
            }

            if (uiState.isLoading) {
                LoadingState(modifier = Modifier.height(200.dp))
            } else if (uiState.errorMessage != null && uiState.recommended.isEmpty() && uiState.nationalHighlights.isEmpty()) {
                ErrorState(uiState.errorMessage.orEmpty(), modifier = Modifier.height(200.dp), onRetry = viewModel::retry)
            } else {
                if (uiState.nationalHighlights.isNotEmpty()) {
                    SectionTitle("Nacionais: destaques")
                    BookRail(uiState.nationalHighlights, onBookClick)
                    Spacer(Modifier.height(16.dp))
                }

                if (uiState.recommended.isNotEmpty()) {
                    SectionTitle("Selecionados para você")
                    BookRail(uiState.recommended, onBookClick)
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun HeroBanner(showAd: Boolean, onReadClick: () -> Unit) {
    if (showAd) {
        // Most visits: a banner ad instead of the quote — see HomeViewModel.AD_PROBABILITY.
        BannerAdView(modifier = Modifier.background(MaterialTheme.colorScheme.secondary))
        return
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
            .padding(20.dp)
    ) {
        Text(
            text = "“O Brasil precisa ser dirigido por uma pessoa que já passou fome. A fome também é professora. Quem passa fome aprende a pensar no próximo, e nas crianças.”",
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSecondary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Carolina Maria de Jesus",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondary
        )
        Spacer(Modifier.height(16.dp))
        LiteraPrimaryButton(
            text = "Ler",
            onClick = onReadClick,
            modifier = Modifier.width(120.dp)
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
private fun ContinueReadingCard(shelfBook: ShelfBook, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        BookCoverImage(
            thumbnailUrl = shelfBook.thumbnailUrl,
            modifier = Modifier
                .width(72.dp)
                .height(108.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(shelfBook.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(shelfBook.authorsLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { shelfBook.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )
            Spacer(Modifier.height(8.dp))
            LiteraPrimaryButton(text = "Continuar", onClick = onClick, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun BookRail(books: List<Book>, onBookClick: (String) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
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
