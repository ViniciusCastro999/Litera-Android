package com.litera.app.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Shapes/colors/padding match the "Botão" component in the LiteraUX Figma
// Style Guide exactly: 100dp (fully rounded) corners, 24dp horizontal /
// 18dp vertical padding, Raleway SemiBold label.
// Primário: bg #5908CF (DaisyBush800) / text #F3F1FF
// Secundário: bg #F4F1FF (DaisyBush50) / text #5908CF (DaisyBush800) — filled, not outlined
// Terciário: text-only, #5908CF

private val LiteraButtonShape = RoundedCornerShape(100.dp)
private val LiteraButtonPadding = PaddingValues(horizontal = 24.dp, vertical = 18.dp)

@Composable
fun LiteraPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = LiteraButtonShape,
        contentPadding = LiteraButtonPadding,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = modifier
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(2.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(text, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}

/**
 * "Botão Secundário": filled with the light Daisy Bush 50 tint, not an
 * outline — matches the real Figma component (bg #F4F1FF, text #5908CF).
 */
@Composable
fun LiteraOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = LiteraButtonShape,
        contentPadding = LiteraButtonPadding,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
    }
}

/** "Botão Terciário": text-only, no container. */
@Composable
fun LiteraTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    androidx.compose.material3.TextButton(
        onClick = onClick,
        enabled = enabled,
        shape = LiteraButtonShape,
        contentPadding = LiteraButtonPadding,
        colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
    }
}
