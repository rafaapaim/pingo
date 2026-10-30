package com.pingo.alertacrypto.presentation.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
private fun PingoTokenPreview(darkTheme: Boolean) {
    PingoTheme(darkTheme) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                PingoLogo()
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusChip("Positivo", StatusChipTone.POSITIVE)
                    StatusChip("Negativo", StatusChipTone.NEGATIVE)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusChip("Atenção", StatusChipTone.WARNING)
                    StatusChip("Neutro", StatusChipTone.NEUTRAL)
                }
            }
        }
    }
}

@Preview(name = "Tokens claros", showBackground = true)
@Composable
private fun PingoTokenPreviewLight() = PingoTokenPreview(darkTheme = false)

@Preview(name = "Tokens escuros", showBackground = true)
@Composable
private fun PingoTokenPreviewDark() = PingoTokenPreview(darkTheme = true)
