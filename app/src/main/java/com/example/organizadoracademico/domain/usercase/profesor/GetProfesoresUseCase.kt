package com.example.organizadoracademico.domain.usercase.profesor

import com.example.organizadoracademico.domain.model.Profesor
import com.example.organizadoracademico.domain.repository.IProfesorRepository
import kotlinx.coroutines.flow.Flow

class GetProfesoresUseCase(
    private val repository: IProfesorRepository
) {
    operator fun invoke(): Flow<List<Profesor>> {
        return repository.getAllProfesores()
    }
}