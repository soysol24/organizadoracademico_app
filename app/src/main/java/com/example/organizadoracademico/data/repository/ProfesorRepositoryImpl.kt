package com.example.organizadoracademico.data.repository

import com.example.organizadoracademico.data.local.dao.ProfesorDao
import com.example.organizadoracademico.data.local.database.AppDatabase
import com.example.organizadoracademico.data.local.entities.toDomain
import com.example.organizadoracademico.data.local.entities.toEntity
import com.example.organizadoracademico.data.local.util.DataInitializer
import com.example.organizadoracademico.data.remote.ProfesorFirestoreService
import com.example.organizadoracademico.domain.model.Profesor
import com.example.organizadoracademico.domain.repository.IProfesorRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch // No olvides esta importación

class ProfesorRepositoryImpl(
    private val dao: ProfesorDao,
    private val remoteService: ProfesorFirestoreService, // Se agregó la coma aquí
    private val db: AppDatabase
) : IProfesorRepository {

    override fun getAllProfesores(): Flow<List<Profesor>> {
        // Ejecutamos la carga inicial en segundo plano
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (dao.getCount() == 0) {
                    // Activamos la carga forzada
                    DataInitializer(db).populateIfEmpty()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Retornamos el Flow que observará los cambios
        return dao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertProfesor(profesor: Profesor) {
        dao.insert(profesor.toEntity())
        try {
            remoteService.save(profesor)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}