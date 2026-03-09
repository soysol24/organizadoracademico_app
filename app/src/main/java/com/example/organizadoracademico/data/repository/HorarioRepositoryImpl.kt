package com.example.organizadoracademico.data.repository

import com.example.organizadoracademico.data.local.dao.HorarioDao
import com.example.organizadoracademico.data.local.entities.toDomain
import com.example.organizadoracademico.data.local.entities.toEntity
import com.example.organizadoracademico.data.remote.HorarioFirestoreService
import com.example.organizadoracademico.domain.model.Horario
import com.example.organizadoracademico.domain.repository.IHorarioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HorarioRepositoryImpl(
    private val dao: HorarioDao,
    private val remoteService: HorarioFirestoreService
) : IHorarioRepository {

    // Ahora pide el userId para filtrar
    override fun getAllHorarios(userId: Int): Flow<List<Horario>> =
        dao.getAllByUsuario(userId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun insertHorario(horario: Horario) {
        dao.insert(horario.toEntity())
        try {
            remoteService.save(horario)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteHorario(id: Int) {
        dao.deleteById(id)
        try {
            remoteService.delete(id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}