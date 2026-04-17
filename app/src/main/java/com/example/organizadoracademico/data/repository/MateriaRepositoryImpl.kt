package com.example.organizadoracademico.data.repository

import com.example.organizadoracademico.data.local.dao.MateriaDao
import com.example.organizadoracademico.data.local.entities.MateriaEntity
import com.example.organizadoracademico.data.local.entities.toDomain
import com.example.organizadoracademico.data.local.entities.toEntity
import com.example.organizadoracademico.data.remote.ApiService
import com.example.organizadoracademico.domain.model.Materia
import com.example.organizadoracademico.domain.repository.IMateriaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MateriaRepositoryImpl(
    private val dao: MateriaDao,
    private val apiService: ApiService
) : IMateriaRepository {

    override fun getAllMaterias(): Flow<List<Materia>> {
        CoroutineScope(Dispatchers.IO).launch {
            syncMateriasDesdeBackend()
        }

        return dao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertMateria(materia: Materia) {
        dao.insert(materia.toEntity())
    }

    override suspend fun deleteMateria(id: Int) {
        dao.deleteById(id)
    }

    private suspend fun syncMateriasDesdeBackend() {
        runCatching {
            val response = apiService.getMaterias()
            if (!response.isSuccessful) return

            response.body().orEmpty().forEach { dto ->
                runCatching {
                    val existing = dao.getByNombre(dto.nombre)
                    dao.insert(
                        MateriaEntity(
                            id = existing?.id ?: 0,
                            nombre = dto.nombre,
                            color = dto.color,
                            icono = dto.icono
                        )
                    )
                }
            }
        }
    }
}