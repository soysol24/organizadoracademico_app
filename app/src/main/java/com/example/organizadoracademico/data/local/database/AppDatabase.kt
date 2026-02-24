package com.example.organizadoracademico.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.organizadoracademico.data.local.dao.*
import com.example.organizadoracademico.data.local.entities.*


@Database(
    entities = [
        UsuarioEntity::class,
        MateriaEntity::class,
        ProfesorEntity::class,
        HorarioEntity::class,
        ImagenEntity::class
    ],
    version = 2, // <-- VERSIÓN INCREMENTADA
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun materiaDao(): MateriaDao
    abstract fun profesorDao(): ProfesorDao
    abstract fun horarioDao(): HorarioDao
    abstract fun imagenDao(): ImagenDao
    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "organizador_academico.db"
                )
                .fallbackToDestructiveMigration() // <-- AÑADIDO
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}