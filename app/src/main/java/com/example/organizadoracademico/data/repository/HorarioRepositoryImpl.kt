package com.example.organizadoracademico.data.repository

import com.example.organizadoracademico.data.local.dao.HorarioDao
import com.example.organizadoracademico.data.local.entities.toDomain
import com.example.organizadoracademico.data.local.entities.toEntity
import com.example.organizadoracademico.domain.model.Horario
import com.example.organizadoracademico.domain.repository.IHorarioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HorarioRepositoryImpl(
    private val dao: HorarioDao
) : IHorarioRepository {

    override fun getAllHorarios(): Flow<List<Horario>> =
        dao.getAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun insertHorario(horario: Horario) =
        dao.insert(horario.toEntity())

    override suspend fun deleteHorario(id: Int) =
        dao.deleteById(id)
}