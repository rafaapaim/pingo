package com.pingo.alertacrypto.presentation.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

enum class StatusChipTone {
    POSITIVE,
    NEGATIVE,
    WARNING,
    NEUTRAL,
}

@Composable
fun StatusChip(
    label: String,
    tone: StatusChipTone,
    modifier: Modifier = Modifier,
) {
    val palette = LocalPingoColors.current
    val colors = when (tone) {
        StatusChipTone.POSITIVE -> palette.positiveChip
        StatusChipTone.NEGATIVE -> palette.negativeChip
        StatusChipTone.WARNING -> palette.warningChip
        StatusChipTone.NEUTRAL -> palette.neutralChip
    }
    Text(
        text = label,
        color = colors.foreground,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(colors.container)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}
