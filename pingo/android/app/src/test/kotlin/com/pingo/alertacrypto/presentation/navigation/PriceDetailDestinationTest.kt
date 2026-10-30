package com.pingo.alertacrypto.presentation.navigation

import com.pingo.alertacrypto.domain.price.Asset
import org.junit.Assert.assertEquals
import org.junit.Test

class PriceDetailDestinationTest {
    @Test
    fun createsStableUniqueDeepLinksAndRequestCodesForSupportedAssets() {
        val links = Asset.supported.map { asset ->
            PriceDetailDestination.uriString(asset.symbol) to
                PriceDetailDestination.requestCode(asset.symbol)
        }

        assertEquals(Asset.supported.map { "pingo://asset/${it.symbol}" }, links.map { it.first })
        assertEquals(Asset.supported.size, links.map { it.second }.toSet().size)
    }
}
