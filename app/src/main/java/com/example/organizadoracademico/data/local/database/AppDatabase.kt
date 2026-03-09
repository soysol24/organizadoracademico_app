package com.example.organizadoracademico.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.organizadoracademico.data.local.dao.*
import com.example.organizadoracademico.data.local.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UsuarioEntity::class, MateriaEntity::class,
        ProfesorEntity::class, HorarioEntity::class, ImagenEntity::class
    ],
    version = 2, // Asegúrate de subir la versión si cambiaste entidades
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
                    .fallbackToDestructiveMigration() // Esto borrará la DB vieja y creará la nueva con usuarioId
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}