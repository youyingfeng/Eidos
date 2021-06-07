package org.eidos.reader

import android.app.Application
import org.eidos.reader.container.AppContainer
import timber.log.Timber

class EidosApplication : Application() {
    // if lazy still crashes then use lateinit var
    val appContainer by lazy { AppContainer(applicationContext) }

    override fun onCreate() {
        super.onCreate()
        // Start logging
        Timber.plant(Timber.DebugTree())
    }

}