package com.example.organizadoracademico.data.repository

import com.example.organizadoracademico.data.local.dao.ProfesorDao
import com.example.organizadoracademico.data.local.entities.ProfesorEntity
import com.example.organizadoracademico.data.local.entities.toDomain
import com.example.organizadoracademico.data.local.entities.toEntity
import com.example.organizadoracademico.data.remote.ApiService
import com.example.organizadoracademico.domain.model.Profesor
import com.example.organizadoracademico.domain.repository.IProfesorRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ProfesorRepositoryImpl(
    private val dao: ProfesorDao,
    private val apiService: ApiService
) : IProfesorRepository {

    override fun getAllProfesores(): Flow<List<Profesor>> {
        CoroutineScope(Dispatchers.IO).launch {
            syncProfesoresDesdeBackend()
        }

        return dao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertProfesor(profesor: Profesor) {
        dao.insert(profesor.toEntity())
    }

    private suspend fun syncProfesoresDesdeBackend() {
        runCatching {
            val response = apiService.getProfesores()
            if (!response.isSuccessful) return

            response.body().orEmpty().forEach { dto ->
                runCatching {
                    dao.insert(ProfesorEntity(id = dto.id, nombre = dto.nombre))
                }
            }
        }
    }
}