package com.litera.app.presentation.explore

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import com.litera.app.presentation.components.CategoryChip
import com.litera.app.presentation.components.ErrorState
import com.litera.app.presentation.components.LiteraTextField
import com.litera.app.presentation.components.LoadingState
import com.litera.app.presentation.components.icons.PhosphorIcons

@Composable
fun ExploreScreen(
    onBookClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    viewModel: ExploreViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LiteraTextField(
                value = uiState.query,
                onValueChange = viewModel::onQueryChange,
                label = "Procurar livro...",
                leadingIcon = { Icon(PhosphorIcons.MagnifyingGlass, contentDescription = null) },
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = {}) {
                Icon(
                    PhosphorIcons.FunnelSimple,
                    contentDescription = "Filtrar",
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Encontre sua próxima leitura",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))

            when {
                uiState.query.isBlank() -> {
                    if (uiState.suggested.isNotEmpty()) {
                        Text(
                            text = "Selecionados para você",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(12.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(uiState.suggested, key = { it.volumeId }) { book ->
                                BookCoverCard(
                                    title = book.title,
                                    authorsLabel = book.authorsLabel,
                                    thumbnailUrl = book.thumbnailUrl,
                                    onClick = { onBookClick(book.volumeId) }
                                )
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                    }

                    Text(
                        text = "Categorias",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(uiState.categories) { category ->
                            CategoryChip(
                                label = category,
                                selected = false,
                                onClick = { onCategoryClick(category) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                uiState.isSearching -> LoadingState(modifier = Modifier.height(200.dp))
                uiState.errorMessage != null -> ErrorState(uiState.errorMessage.orEmpty())
                uiState.results.isEmpty() -> Text(
                    "Nenhum livro encontrado para \"${uiState.query}\".",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                else -> LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(uiState.results, key = { it.volumeId }) { book ->
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
}
