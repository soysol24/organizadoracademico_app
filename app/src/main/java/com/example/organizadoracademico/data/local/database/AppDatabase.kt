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
    version = 2,
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
                    .build()

                INSTANCE = instance

                // DISPARO DIRECTO: Cada vez que se crea la instancia,
                // intentamos poblar si está vacía.
                CoroutineScope(Dispatchers.IO).launch {
                    prePopulate(instance)
                }

                instance
            }
        }

        private suspend fun prePopulate(db: AppDatabase) {
            // Verificamos si ya hay datos para no duplicar cada vez que abra la app
            // Nota: Asegúrate de tener un método que no sea Flow para esta comprobación rápida
            try {
                // Solo insertamos si la tabla de materias está vacía
                // Si prefieres hacerlo "a la fuerza" siempre, quita el if
                val materiasExistentes = db.materiaDao().getAllStatic()
                if (materiasExistentes.isEmpty()) {
                    val materias = listOf(
                        MateriaEntity(nombre = "Programación Móvil", color = "Morado", icono = "📱"),
                        MateriaEntity(nombre = "Bases de Datos", color = "Azul", icono = "🗄️"),
                        MateriaEntity(nombre = "Estructuras de Datos", color = "Verde", icono = "🌲"),
                        MateriaEntity(nombre = "Programación Web", color = "Naranja", icono = "💻"),
                        MateriaEntity(nombre = "Sistemas Operativos", color = "Rojo", icono = "🔧"),
                        MateriaEntity(nombre = "Ingeniería de Software", color = "Rosa", icono = "📊")
                    )
                    materias.forEach { db.materiaDao().insert(it) }

                    val profesores = listOf(
                        ProfesorEntity(nombre = "Dr. Carlos"),
                        ProfesorEntity(nombre = "Mtro. Alonso M"),
                        ProfesorEntity(nombre = "Mtro. Horacio"),
                        ProfesorEntity(nombre = "Mtra. Diana"),
                        ProfesorEntity(nombre = "Mtro. Renan"),
                        ProfesorEntity(nombre = "Mtro. Ali")
                    )
                    profesores.forEach { db.profesorDao().insert(it) }
                }
            } catch (e: Exception) {
                // Si da error es porque quizás la tabla aún no existe o está migrando
                e.printStackTrace()
            }
        }
    }
}