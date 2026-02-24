package com.example.organizadoracademico.data.repository

import com.example.organizadoracademico.data.local.dao.MateriaDao
import com.example.organizadoracademico.data.local.entities.toDomain
import com.example.organizadoracademico.data.local.entities.toEntity
import com.example.organizadoracademico.domain.model.Materia
import com.example.organizadoracademico.domain.repository.IMateriaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MateriaRepositoryImpl(
    private val dao: MateriaDao
) : IMateriaRepository {

    override fun getAllMaterias(): Flow<List<Materia>> =
        dao.getAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun insertMateria(materia: Materia) {
        dao.insert(materia.toEntity())
    }

    override suspend fun deleteMateria(id: Int) {
        dao.deleteById(id)
    }
}