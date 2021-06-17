package org.eidos.reader

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import org.eidos.reader.container.AppContainer
import timber.log.Timber

class EidosApplication : Application() {
    // if lazy still crashes then use lateinit var
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private lateinit var _appContainer: AppContainer
    val appContainer: AppContainer
        get() = _appContainer

    override fun onCreate() {
        super.onCreate()
        _appContainer = AppContainer(applicationContext, dataStore)
        // Start logging
        Timber.plant(Timber.DebugTree())
    }

}
