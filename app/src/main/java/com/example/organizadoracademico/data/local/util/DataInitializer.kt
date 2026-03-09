package com.example.organizadoracademico.data.local.util

import com.example.organizadoracademico.data.local.database.AppDatabase
import com.example.organizadoracademico.data.local.entities.MateriaEntity
import com.example.organizadoracademico.data.local.entities.ProfesorEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataInitializer(private val db: AppDatabase) {

    suspend fun populateIfEmpty() = withContext(Dispatchers.IO) {
        // Usamos el conteo general, sin filtrar por usuario
        if (db.materiaDao().getCountGlobal() == 0) {

            val profesores = listOf(
                ProfesorEntity(nombre = "Dr. Carlos"),
                ProfesorEntity(nombre = "Mtro. Alonso M"),
                ProfesorEntity(nombre = "Mtro. Horacio"),
                ProfesorEntity(nombre = "Mtra. Diana"),
                ProfesorEntity(nombre = "Mtro. Renan"),
                ProfesorEntity(nombre = "Mtro. Ali")
            )
            profesores.forEach { db.profesorDao().insert(it) }

            val materias = listOf(
                MateriaEntity(nombre = "Programación Móvil", color = "Morado", icono = "📱"),
                MateriaEntity(nombre = "Bases de Datos", color = "Azul", icono = "🗄️"),
                MateriaEntity(nombre = "Estructuras de Datos", color = "Verde", icono = "🌲"),
                MateriaEntity(nombre = "Programación Web", color = "Naranja", icono = "💻"),
                MateriaEntity(nombre = "Sistemas Operativos", color = "Rojo", icono = "🔧"),
                MateriaEntity(nombre = "Ingeniería de Software", color = "Rosa", icono = "📊")
            )
            materias.forEach { db.materiaDao().insert(it) }
        }
    }
}