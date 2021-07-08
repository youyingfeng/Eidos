package org.eidos.reader.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.preference.PreferenceDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class SettingsDataStore
    constructor(
        private val dataStore: DataStore<Preferences>
    )
    : PreferenceDataStore()
{
    override fun putBoolean(key: String?, value: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit { preferences ->
                preferences[booleanPreferencesKey(key!!)] = value
            }
        }
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return runBlocking {
            dataStore.data.map { preferences ->
                preferences[booleanPreferencesKey(key!!)] ?: defValue
            }
            .first()
        }
    }

    override fun putFloat(key: String?, value: Float) {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit { preferences ->
                preferences[floatPreferencesKey(key!!)] = value
            }
        }
    }

    override fun getFloat(key: String?, defValue: Float): Float {
        return runBlocking {
            dataStore.data.map { preferences ->
                preferences[floatPreferencesKey(key!!)] ?: defValue
            }
            .first()
        }
    }
}