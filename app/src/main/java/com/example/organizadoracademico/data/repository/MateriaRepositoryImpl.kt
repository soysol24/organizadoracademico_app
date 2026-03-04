package com.example.organizadoracademico.data.repository

import com.example.organizadoracademico.data.local.dao.MateriaDao
import com.example.organizadoracademico.data.local.database.AppDatabase
import com.example.organizadoracademico.data.local.entities.toDomain
import com.example.organizadoracademico.data.local.entities.toEntity
import com.example.organizadoracademico.data.local.util.DataInitializer
import com.example.organizadoracademico.data.remote.MateriaFirestoreService
import com.example.organizadoracademico.domain.model.Materia
import com.example.organizadoracademico.domain.repository.IMateriaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MateriaRepositoryImpl(
    private val dao: MateriaDao,
    private val remoteService: MateriaFirestoreService,
    private val db: AppDatabase
) : IMateriaRepository {

    override fun getAllMaterias(): Flow<List<Materia>> {
        // Disparo de carga forzada: Si no hay datos, DataInitializer los crea
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Usamos el conteo de profesores como bandera de base de datos vacía
                if (db.profesorDao().getCount() == 0) {
                    DataInitializer(db).populateIfEmpty()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return dao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertMateria(materia: Materia) {
        // 1. Persistencia local inmediata
        dao.insert(materia.toEntity())
        
        // 2. Sincronización remota
        try {
            remoteService.saveMateria(materia)
        } catch (e: Exception) {
            // Aquí se podría implementar un sistema de reintentos o marcar como "pendiente"
            e.printStackTrace()
        }
    }

    override suspend fun deleteMateria(id: Int) {
        // 1. Borrado local
        dao.deleteById(id)
        
        // 2. Borrado remoto
        try {
            remoteService.deleteMateria(id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}