package com.pingo.alertacrypto.presentation.theme

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pingo.alertacrypto.domain.price.Asset
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.pingoPreferencesDataStore by preferencesDataStore(name = "pingo_preferences")

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
    ;

    fun resolvesDarkTheme(systemDarkTheme: Boolean): Boolean = when (this) {
        SYSTEM -> systemDarkTheme
        LIGHT -> false
        DARK -> true
    }

    companion object {
        fun fromStoredValue(value: String?): ThemeMode =
            entries.firstOrNull { it.name == value } ?: SYSTEM
    }
}

data class PingoPreferences(
    val favoriteSymbols: Set<String> = emptySet(),
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
)

class ThemePreferenceStore @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val dataStore = context.applicationContext.pingoPreferencesDataStore

    val preferences: Flow<PingoPreferences> = dataStore.data.map { stored ->
        val supportedSymbols = Asset.entries.mapTo(mutableSetOf()) { it.symbol }
        PingoPreferences(
            favoriteSymbols = stored[FAVORITE_SYMBOLS].orEmpty()
                .filterTo(mutableSetOf()) { it in supportedSymbols },
            themeMode = ThemeMode.fromStoredValue(stored[THEME_MODE]),
        )
    }

    val themeMode: Flow<ThemeMode> = preferences.map { it.themeMode }
    val favoriteSymbols: Flow<Set<String>> = preferences.map { it.favoriteSymbols }

    suspend fun setThemeMode(themeMode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE] = themeMode.name
        }
    }

    suspend fun setFavorite(symbol: String, favorite: Boolean) {
        val supportedSymbol = Asset.fromSymbol(symbol)?.symbol ?: return
        dataStore.edit { preferences ->
            val favorites = preferences[FAVORITE_SYMBOLS].orEmpty()
                .filterTo(mutableSetOf()) { Asset.fromSymbol(it) != null }
            if (favorite) favorites += supportedSymbol else favorites -= supportedSymbol
            preferences[FAVORITE_SYMBOLS] = favorites
        }
    }

    private companion object {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val FAVORITE_SYMBOLS = stringSetPreferencesKey("favorite_symbols")
    }
}

object FavoriteAssetOrdering {
    fun order(
        favoriteSymbols: Set<String>,
        assets: List<Asset> = Asset.supported,
    ): List<Asset> = assets.sortedWith(
        compareByDescending<Asset> { it.symbol in favoriteSymbols }
            .thenBy { Asset.supported.indexOf(it) },
    )
}
