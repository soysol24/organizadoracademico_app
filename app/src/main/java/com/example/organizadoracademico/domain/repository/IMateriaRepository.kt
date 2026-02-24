package com.example.organizadoracademico.domain.repository

import com.example.organizadoracademico.domain.model.Materia
import kotlinx.coroutines.flow.Flow

interface IMateriaRepository {
    fun getAllMaterias(): Flow<List<Materia>>
    suspend fun insertMateria(materia: Materia)
    suspend fun deleteMateria(id: Int)
}