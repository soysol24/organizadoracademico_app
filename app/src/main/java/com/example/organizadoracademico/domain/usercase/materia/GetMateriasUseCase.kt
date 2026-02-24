package com.example.organizadoracademico.domain.usercase.materia

import com.example.organizadoracademico.domain.model.Materia
import com.example.organizadoracademico.domain.repository.IMateriaRepository
import kotlinx.coroutines.flow.Flow

class GetMateriasUseCase(
    private val repository: IMateriaRepository
) {
    operator fun invoke(): Flow<List<Materia>> {
        return repository.getAllMaterias()
    }
}