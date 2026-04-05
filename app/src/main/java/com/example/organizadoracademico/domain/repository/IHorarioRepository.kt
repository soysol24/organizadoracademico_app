package com.example.organizadoracademico.domain.repository

import com.example.organizadoracademico.domain.model.Horario
import kotlinx.coroutines.flow.Flow

interface IHorarioRepository {
    // ACTUALIZADO: Ahora pide el userId
    fun getAllHorarios(userId: Int): Flow<List<Horario>>

    suspend fun insertHorario(horario: Horario)
    suspend fun updateHorario(horario: Horario)
    suspend fun deleteHorario(id: Int)
}