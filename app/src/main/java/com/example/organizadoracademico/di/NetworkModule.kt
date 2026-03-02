package com.example.organizadoracademico.di

import com.example.organizadoracademico.data.remote.*
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.dsl.module

val networkModule = module {
    single { FirebaseFirestore.getInstance() }
    single { MateriaFirestoreService(get()) }
    single { ProfesorFirestoreService(get()) }
    single { HorarioFirestoreService(get()) }
    single { ImagenFirestoreService(get()) }
    single { UsuarioFirestoreService(get()) }
}