package com.example.organizadoracademico.domain.repository

import com.example.organizadoracademico.domain.model.Profesor
import kotlinx.coroutines.flow.Flow

interface IProfesorRepository {
    fun getAllProfesores(): Flow<List<Profesor>>
    suspend fun insertProfesor(profesor: Profesor)
}