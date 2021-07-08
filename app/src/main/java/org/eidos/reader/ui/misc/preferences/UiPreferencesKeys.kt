package org.eidos.reader.ui.misc.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey

object UiPreferencesKeys {
    val USE_NIGHT_MODE = booleanPreferencesKey("useNightMode")
    val READER_TEXT_SIZE = floatPreferencesKey("reader_text_size")
}
