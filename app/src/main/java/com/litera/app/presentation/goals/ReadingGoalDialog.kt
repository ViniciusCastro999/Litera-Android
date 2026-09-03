package com.litera.app.presentation.goals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.litera.app.domain.model.ReadingGoal
import com.litera.app.domain.model.ReadingGoalType
import com.litera.app.presentation.components.CategoryChip
import com.litera.app.presentation.components.LiteraTextField

private fun labelFor(type: ReadingGoalType): String = when (type) {
    ReadingGoalType.PAGES_PER_WEEK -> "Páginas/semana"
    ReadingGoalType.BOOKS_PER_MONTH -> "Livros/mês"
    ReadingGoalType.NATIONAL_BOOKS -> "Livros nacionais"
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReadingGoalDialog(
    editingGoal: ReadingGoal?,
    onDismiss: () -> Unit,
    onConfirm: (label: String, type: ReadingGoalType, targetValue: Int) -> Unit
) {
    var labelText by remember { mutableStateOf(editingGoal?.label.orEmpty()) }
    var selectedType by remember { mutableStateOf(editingGoal?.type ?: ReadingGoalType.PAGES_PER_WEEK) }
    var targetValueText by remember {
        mutableStateOf(if ((editingGoal?.targetValue ?: 0) > 0) editingGoal!!.targetValue.toString() else "")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (editingGoal == null) "Criar meta" else "Editar meta") },
        text = {
            Column {
                LiteraTextField(
                    value = labelText,
                    onValueChange = { labelText = it },
                    label = "Descrição da meta"
                )
                Spacer(Modifier.height(12.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ReadingGoalType.values().forEach { type ->
                        CategoryChip(
                            label = labelFor(type),
                            selected = selectedType == type,
                            onClick = { selectedType = type }
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                LiteraTextField(
                    value = targetValueText,
                    onValueChange = { targetValueText = it.filter { c -> c.isDigit() } },
                    label = "Valor alvo",
                    keyboardType = KeyboardType.Number
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val targetValue = targetValueText.toIntOrNull() ?: 0
                onConfirm(labelText, selectedType, targetValue)
            }) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
