package com.pingo.alertacrypto.presentation.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pingo.alertacrypto.presentation.theme.PingoLogo

@Composable
internal fun PingoAppHeader(
    statusBarInsets: WindowInsets = WindowInsets.statusBars,
) {
    PingoLogo(
        modifier = Modifier
            .windowInsetsPadding(statusBarInsets)
            .padding(start = 20.dp, top = 12.dp, bottom = 8.dp),
    )
}
