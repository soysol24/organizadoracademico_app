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
        UsuarioEntity::class,
        MateriaEntity::class,
        ProfesorEntity::class,
        HorarioEntity::class,
        ImagenEntity::class
    ],
    version = 3,
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
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(context))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let {
                CoroutineScope(Dispatchers.IO).launch {
                    prePopulate(it)
                }
            }
        }

        suspend fun prePopulate(database: AppDatabase) {
            val materias = listOf(
                MateriaEntity(nombre = "Programación Móvil", color = "Morado", icono = "📱"),
                MateriaEntity(nombre = "Bases de Datos", color = "Azul", icono = "🗄️"),
                MateriaEntity(nombre = "Estructuras de Datos", color = "Verde", icono = "🌲"),
                MateriaEntity(nombre = "Programación Web", color = "Naranja", icono = "💻"),
                MateriaEntity(nombre = "Sistemas Operativos", color = "Rojo", icono = "🔧"),
                MateriaEntity(nombre = "Ingeniería de Software", color = "Rosa", icono = "📊")
            )
            materias.forEach { database.materiaDao().insert(it) }

            val profesores = listOf(
                ProfesorEntity(nombre = "Dr. Carlos"),
                ProfesorEntity(nombre = "Mtro. Alonso M"),
                ProfesorEntity(nombre = "Mtro. Horacio"),
                ProfesorEntity(nombre = "Mtra. Diana"),
                ProfesorEntity(nombre = "Mtro. Renan"),
                ProfesorEntity(nombre = "Mtro. Ali")
            )
            profesores.forEach { database.profesorDao().insert(it) }
        }
    }
}