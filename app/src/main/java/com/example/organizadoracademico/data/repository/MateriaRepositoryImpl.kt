package com.example.organizadoracademico.data.repository

import com.example.organizadoracademico.data.local.dao.MateriaDao
import com.example.organizadoracademico.data.local.entities.toDomain
import com.example.organizadoracademico.data.local.entities.toEntity
import com.example.organizadoracademico.data.remote.MateriaFirestoreService
import com.example.organizadoracademico.domain.model.Materia
import com.example.organizadoracademico.domain.repository.IMateriaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MateriaRepositoryImpl(
    private val dao: MateriaDao,
    private val remoteService: MateriaFirestoreService
) : IMateriaRepository {

    override fun getAllMaterias(): Flow<List<Materia>> =
        dao.getAll().map { entities -> entities.map { it.toDomain() } }

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