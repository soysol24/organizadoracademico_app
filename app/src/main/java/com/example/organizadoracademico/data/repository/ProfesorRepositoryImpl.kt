package com.example.organizadoracademico.data.repository

import com.example.organizadoracademico.data.local.dao.ProfesorDao
import com.example.organizadoracademico.data.local.entities.toDomain
import com.example.organizadoracademico.data.local.entities.toEntity
import com.example.organizadoracademico.domain.model.Profesor
import com.example.organizadoracademico.domain.repository.IProfesorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfesorRepositoryImpl(
    private val dao: ProfesorDao
) : IProfesorRepository {

    override fun getAllProfesores(): Flow<List<Profesor>> =
        dao.getAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun insertProfesor(profesor: Profesor) {
        dao.insert(profesor.toEntity())
    }
}