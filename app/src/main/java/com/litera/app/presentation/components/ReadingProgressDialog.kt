package com.litera.app.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun ReadingProgressDialog(
    initialCurrentPage: Int,
    initialTotalPages: Int,
    onDismiss: () -> Unit,
    onConfirm: (currentPage: Int, totalPages: Int) -> Unit
) {
    var currentPageText by remember { mutableStateOf(if (initialCurrentPage > 0) initialCurrentPage.toString() else "") }
    var totalPagesText by remember { mutableStateOf(if (initialTotalPages > 0) initialTotalPages.toString() else "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Em qual página você está?") },
        text = {
            Column {
                LiteraTextField(
                    value = currentPageText,
                    onValueChange = { currentPageText = it.filter { c -> c.isDigit() } },
                    label = "Página atual",
                    keyboardType = KeyboardType.Number
                )
                Spacer(Modifier.height(12.dp))
                LiteraTextField(
                    value = totalPagesText,
                    onValueChange = { totalPagesText = it.filter { c -> c.isDigit() } },
                    label = "Total de páginas",
                    keyboardType = KeyboardType.Number
                )
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = {
                val current = currentPageText.toIntOrNull() ?: 0
                val total = totalPagesText.toIntOrNull() ?: initialTotalPages
                onConfirm(current, total)
            }) {
                Text("Salvar")
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
