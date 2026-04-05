package com.example.organizadoracademico.di

import com.example.organizadoracademico.data.sync.SyncScheduler
import com.example.organizadoracademico.data.sync.WorkManagerSyncScheduler
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val syncModule = module {
    single<SyncScheduler> { WorkManagerSyncScheduler(androidContext()) }
}

