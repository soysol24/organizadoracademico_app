package com.example.organizadoracademico.di

import android.content.Context
import androidx.room.Room
import com.example.organizadoracademico.data.local.dao.*
import com.example.organizadoracademico.data.local.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {

    single<AppDatabase> {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "organizador_academico.db"
        ).build()
    }

    single<MateriaDao> { get<AppDatabase>().materiaDao() }
    single<ProfesorDao> { get<AppDatabase>().profesorDao() }
    single<HorarioDao> { get<AppDatabase>().horarioDao() }
    single<ImagenDao> { get<AppDatabase>().imagenDao() }
    single<UsuarioDao> { get<AppDatabase>().usuarioDao() }

}