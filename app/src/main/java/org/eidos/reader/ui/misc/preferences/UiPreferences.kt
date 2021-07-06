package org.eidos.reader.ui.misc.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey

data class UiPreferences(
    val useNightMode: Boolean
)

object UiPreferencesKeys {
    val USE_NIGHT_MODE = booleanPreferencesKey("useNightMode")
}