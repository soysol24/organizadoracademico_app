package com.example.organizadoracademico.di

import org.koin.core.module.Module

// Este archivo une todos los módulos de la aplicación.
// Asegúrate de que tu clase Application inicialice Koin con esta lista.
val appModules: List<Module> = listOf(
    databaseModule,   // Módulo de Room Database
    networkModule,    // Módulo de Retrofit
    syncModule,       // WorkManager + scheduler de sincronización
    repositoryModule, // Módulo de Repositorios
    hardwareModule,   // Módulo de Hardware (cámara, vibrador, etc.)
    useCaseModule,    // Módulo de Casos de Uso
    viewModelModule   // Módulo de ViewModels
)