package org.eidos.reader

import android.app.Application
import timber.log.Timber

class EidosApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Start logging
        Timber.plant(Timber.DebugTree())
    }

}