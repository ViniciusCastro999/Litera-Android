package com.litera.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.litera.app.core.theme.Bunker50
import com.litera.app.core.theme.DaisyBush400
import com.litera.app.core.theme.DaisyBush800

/**
 * "Categoria de livro" component from the LiteraUX Figma Style Guide: a
 * filled 16dp-rounded-rect chip (not a Material pill), bg #A178FF, text
 * #F7F8F8, Raleway SemiBold 16sp. Selected state deepens to the primary
 * Daisy Bush 800 so it still reads clearly against the unselected chips.
 */
@Composable
fun CategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor: Color = if (selected) DaisyBush800 else DaisyBush400
    Text(
        text = label,
        color = Bunker50,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}
