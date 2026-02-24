package com.example.organizadoracademico

import android.app.Application
import com.example.organizadoracademico.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class OrganizadorApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@OrganizadorApplication)
            modules(appModules)
        }
    }
}