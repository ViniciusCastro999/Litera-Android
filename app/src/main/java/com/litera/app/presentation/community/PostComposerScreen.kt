package com.litera.app.presentation.community

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.litera.app.presentation.components.CategoryChip
import com.litera.app.presentation.components.LiteraPrimaryButton
import com.litera.app.presentation.components.icons.PhosphorIcons

/** Suggested starter tags shown alongside whatever the user has already added. */
private val SuggestedTags = listOf("Poesia", "Ficção", "Romance", "Fantasia", "Não ficção")

@Composable
fun PostComposerScreen(
    onBack: () -> Unit,
    onPosted: () -> Unit,
    viewModel: PostComposerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddTagDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.postCreated) {
        if (uiState.postCreated) onPosted()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(PhosphorIcons.ArrowLeft, contentDescription = "Voltar")
            }
            Spacer(Modifier.weight(1f))
            LiteraPrimaryButton(
                text = "Postar",
                onClick = viewModel::createPost,
                enabled = uiState.canPost,
                isLoading = uiState.isPosting
            )
        }

        OutlinedTextField(
            value = uiState.text,
            onValueChange = viewModel::updateText,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            placeholder = { Text("O que você está lendo?") },
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent
            )
        )

        val allTagOptions = remember(uiState.tags) {
            (SuggestedTags + uiState.tags).distinct()
        }

        LazyRow(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            items(allTagOptions, key = { it }) { tag ->
                val selected = uiState.tags.contains(tag)
                CategoryChip(
                    label = if (selected) "− $tag" else "+ $tag",
                    selected = selected,
                    onClick = { viewModel.toggleTag(tag) },
                    modifier = Modifier.wrapContentWidth()
                )
            }
            item {
                CategoryChip(
                    label = "+ Add tag",
                    selected = false,
                    onClick = { showAddTagDialog = true },
                    modifier = Modifier.wrapContentWidth()
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Visual-only stubs — no image/GIF/link/video picker wired up.
            Icon(PhosphorIcons.Camera, contentDescription = "Imagem", tint = MaterialTheme.colorScheme.primary)
            Text("GIF", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
            Icon(PhosphorIcons.Share, contentDescription = "Link", tint = MaterialTheme.colorScheme.primary)
            Icon(PhosphorIcons.PlayCircle, contentDescription = "Vídeo", tint = MaterialTheme.colorScheme.primary)
        }
    }

    if (showAddTagDialog) {
        AddTagDialog(
            onDismiss = { showAddTagDialog = false },
            onConfirm = { tag ->
                viewModel.addTag(tag)
                showAddTagDialog = false
            }
        )
    }
}

@Composable
private fun AddTagDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var tagText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Adicionar tag") },
        text = {
            OutlinedTextField(
                value = tagText,
                onValueChange = { tagText = it },
                singleLine = true,
                placeholder = { Text("Nome da tag") }
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(tagText) }, enabled = tagText.isNotBlank()) {
                Text("Adicionar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
