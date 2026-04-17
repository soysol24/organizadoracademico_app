package com.example.organizadoracademico.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.organizadoracademico.data.local.dao.HorarioDao
import com.example.organizadoracademico.data.local.dao.ImagenDao
import com.example.organizadoracademico.data.local.dao.MateriaDao
import com.example.organizadoracademico.data.local.dao.ProfesorDao
import com.example.organizadoracademico.data.local.dao.SyncQueueDao
import com.example.organizadoracademico.data.local.dao.UsuarioDao
import com.example.organizadoracademico.data.local.entities.HorarioEntity
import com.example.organizadoracademico.data.local.entities.ImagenEntity
import com.example.organizadoracademico.data.local.entities.MateriaEntity
import com.example.organizadoracademico.data.local.entities.ProfesorEntity
import com.example.organizadoracademico.data.local.entities.SyncQueueEntity
import com.example.organizadoracademico.data.local.entities.UsuarioEntity

@Database(
    entities = [
        UsuarioEntity::class,
        MateriaEntity::class,
        ProfesorEntity::class,
        HorarioEntity::class,
        ImagenEntity::class,
        SyncQueueEntity::class
    ],
    version = 6,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun materiaDao(): MateriaDao
    abstract fun profesorDao(): ProfesorDao
    abstract fun horarioDao(): HorarioDao
    abstract fun imagenDao(): ImagenDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun syncQueueDao(): SyncQueueDao

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
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}