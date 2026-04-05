package com.example.organizadoracademico.domain.repository

import com.example.organizadoracademico.domain.model.Materia
import kotlinx.coroutines.flow.Flow

interface IMateriaRepository {
    // CAMBIO: Quita el parámetro (userId: Int) y renombra si quieres a getAllMaterias
    fun getAllMaterias(): Flow<List<Materia>>

    suspend fun insertMateria(materia: Materia)
    suspend fun deleteMateria(id: Int)
}