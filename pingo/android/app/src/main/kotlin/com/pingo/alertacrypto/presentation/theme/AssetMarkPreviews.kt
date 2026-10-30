package com.pingo.alertacrypto.presentation.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pingo.alertacrypto.domain.price.Asset

@Composable
private fun AssetMarksPreview(darkTheme: Boolean) {
    PingoTheme(darkTheme) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Asset.supported.chunked(3).forEach { assets ->
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        assets.forEach { asset ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                AssetMark(asset = asset, size = 48.dp)
                                Text(
                                    text = asset.symbol,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Logos · tema claro", showBackground = true)
@Composable
private fun AssetMarksLightPreview() = AssetMarksPreview(darkTheme = false)

@Preview(name = "Logos · tema escuro", showBackground = true)
@Composable
private fun AssetMarksDarkPreview() = AssetMarksPreview(darkTheme = true)
