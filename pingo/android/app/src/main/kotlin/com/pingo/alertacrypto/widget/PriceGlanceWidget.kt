package com.pingo.alertacrypto.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.color.ColorProvider as DayNightColorProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.LocalSize
import androidx.glance.unit.ColorProvider
import com.pingo.alertacrypto.data.local.entity.toPriceTick
import com.pingo.alertacrypto.presentation.navigation.PriceDetailDestination
import dagger.hilt.android.EntryPointAccessors

class PriceGlanceWidget : GlanceAppWidget() {
    override val stateDefinition = PriceWidgetStateDefinition
    override val sizeMode = SizeMode.Responsive(
        setOf(
            DpSize(180.dp, 80.dp),
            DpSize(180.dp, 180.dp),
        ),
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val preferences = getAppWidgetState(context, stateDefinition, id)
        val symbol = preferences[PriceWidgetSymbolKey] ?: DEFAULT_SYMBOL
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            PriceWidgetEntryPoint::class.java,
        )
        val latest = entryPoint.latestPriceDao().getBySymbol(symbol)
        val widgetState = PriceWidgetStateMapper.map(
            tick = latest?.toPriceTick(),
            receivedAtMillis = latest?.receivedAtEpochMillis,
            nowMillis = System.currentTimeMillis(),
            selectedSymbol = symbol,
        )
        val colors = widgetColors()
        val detailIntent = PriceDetailDestination.intent(context, symbol)
        provideContent {
            PriceWidgetContent(
                state = widgetState,
                isExpanded = LocalSize.current.height >= 140.dp,
                background = colors.first,
                foreground = colors.second,
                detailIntent = detailIntent,
            )
        }
    }

    @Composable
    private fun PriceWidgetContent(
        state: PriceWidgetState,
        isExpanded: Boolean,
        background: ColorProvider,
        foreground: ColorProvider,
        detailIntent: android.content.Intent,
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(background)
                .padding(12.dp)
                .clickable(actionStartActivity(detailIntent)),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.Start,
        ) {
            Row(
                verticalAlignment = Alignment.Vertical.CenterVertically,
                horizontalAlignment = Alignment.Horizontal.Start,
            ) {
                Image(
                    provider = ImageProvider(com.pingo.alertacrypto.R.drawable.ic_pingo_mark),
                    contentDescription = null,
                    modifier = GlanceModifier.size(18.dp),
                )
                Text(
                    text = state.symbol,
                    style = TextStyle(color = foreground),
                    modifier = GlanceModifier.padding(start = 8.dp),
                )
                Text(
                    text = if (state.freshness == com.pingo.alertacrypto.domain.monitoring.QuoteFreshness.FRESH) {
                        state.priceText
                    } else {
                        state.statusText
                    },
                    style = TextStyle(color = foreground),
                    modifier = GlanceModifier.padding(start = 8.dp),
                )
            }
            if (isExpanded && state.showPrice) {
                Text(
                    text = state.changeText,
                    style = TextStyle(color = foreground),
                    modifier = GlanceModifier.padding(top = 8.dp),
                )
                Text(
                    text = state.updatedAtText,
                    style = TextStyle(color = foreground),
                    modifier = GlanceModifier.padding(top = 4.dp),
                )
            } else if (
                isExpanded &&
                state.freshness == com.pingo.alertacrypto.domain.monitoring.QuoteFreshness.STALE
            ) {
                Text(
                    text = "Último preço: ${state.priceText}",
                    style = TextStyle(color = foreground),
                )
                Text(
                    text = "Atualizado ${state.updatedAtText}",
                    style = TextStyle(color = foreground),
                )
            } else if (isExpanded) {
                Text(state.priceText, style = TextStyle(color = foreground))
            }
        }
    }

    private fun widgetColors(): Pair<ColorProvider, ColorProvider> {
        return DayNightColorProvider(Color(0xFFF7F5FB), Color(0xFF141218)) to
            DayNightColorProvider(Color(0xFF171522), Color(0xFFE6E1E9))
    }

    companion object {
        const val DEFAULT_SYMBOL = DEFAULT_WIDGET_SYMBOL
    }
}

class PriceGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = PriceGlanceWidget()
}
