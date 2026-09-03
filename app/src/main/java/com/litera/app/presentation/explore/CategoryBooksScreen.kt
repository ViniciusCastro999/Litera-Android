package com.litera.app.presentation.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import com.litera.app.presentation.components.icons.PhosphorIcons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.litera.app.presentation.components.BookCoverCard
import com.litera.app.presentation.components.ErrorState
import com.litera.app.presentation.components.LoadingState

@Composable
fun CategoryBooksScreen(
    onBack: () -> Unit,
    onBookClick: (String) -> Unit,
    viewModel: CategoryBooksViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(PhosphorIcons.ArrowLeft, contentDescription = "Voltar")
            }
            Text(uiState.category, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }

        when {
            uiState.isLoading -> LoadingState()
            uiState.errorMessage != null -> ErrorState(uiState.errorMessage.orEmpty(), onRetry = viewModel::load)
            uiState.books.isEmpty() -> Text(
                "Nenhum livro encontrado nessa categoria ainda.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(uiState.books, key = { it.volumeId }) { book ->
                    BookCoverCard(
                        title = book.title,
                        authorsLabel = book.authorsLabel,
                        thumbnailUrl = book.thumbnailUrl,
                        onClick = { onBookClick(book.volumeId) },
                        width = 100.dp
                    )
                }
            }
        }
    }
}
