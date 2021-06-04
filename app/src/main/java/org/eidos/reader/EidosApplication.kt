package org.eidos.reader

import android.app.Application
import org.eidos.reader.container.AppContainer
import timber.log.Timber

class EidosApplication : Application() {
    val appContainer = AppContainer(this)

    override fun onCreate() {
        super.onCreate()
        // Start logging
        Timber.plant(Timber.DebugTree())
    }

}