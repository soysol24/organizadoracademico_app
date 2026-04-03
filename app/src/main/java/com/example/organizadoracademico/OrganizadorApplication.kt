package com.example.organizadoracademico

import android.app.Application
import com.example.organizadoracademico.data.sync.SyncScheduler
import com.example.organizadoracademico.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class OrganizadorApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val koinApp = startKoin {
            androidContext(this@OrganizadorApplication)
            modules(appModules)
        }

        koinApp.koin.get<SyncScheduler>().schedulePeriodic()
    }
}