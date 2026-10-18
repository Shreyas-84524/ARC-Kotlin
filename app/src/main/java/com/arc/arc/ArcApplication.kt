package com.arc.arc

import android.app.Application
import com.arc.arc.core.di.AppContainer
import com.arc.arc.core.di.DefaultAppContainer

/**
 * Base Application class for ARC.
 * Holds the root dependency container.
 */
class ArcApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
