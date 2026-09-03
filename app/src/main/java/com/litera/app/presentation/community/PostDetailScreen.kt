package com.litera.app.presentation.community

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.litera.app.domain.model.Comment
import com.litera.app.presentation.components.LoadingState
import com.litera.app.presentation.components.icons.PhosphorIcons

@Composable
fun PostDetailScreen(
    onBack: () -> Unit,
    viewModel: PostDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(PhosphorIcons.ArrowLeft, contentDescription = "Voltar")
            }
        }

        val post = uiState.post
        when {
            uiState.isLoading -> LoadingState()
            post == null -> Text(
                text = "Publicação não encontrada.",
                modifier = Modifier.padding(16.dp)
            )
            else -> {
                LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    item {
                        PostCard(
                            post = post,
                            onLikeClick = viewModel::toggleLike,
                            onClick = {}
                        )
                    }
                    item {
                        Text(
                            text = "Comentários",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    items(uiState.comments, key = { it.id }) { comment ->
                        CommentRow(comment)
                    }
                }

                CommentInputRow(
                    value = uiState.commentText,
                    onValueChange = viewModel::updateCommentText,
                    onSend = viewModel::sendComment
                )
            }
        }
    }
}

@Composable
private fun CommentRow(comment: Comment) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = comment.authorName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = comment.text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun CommentInputRow(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Adicionar comentário...") },
            singleLine = true
        )
        IconButton(onClick = onSend, enabled = value.isNotBlank()) {
            Icon(
                imageVector = PhosphorIcons.Share,
                contentDescription = "Enviar comentário",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
