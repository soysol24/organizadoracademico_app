package com.example.organizadoracademico.domain.usercase.materia

import com.example.organizadoracademico.domain.model.Materia
import com.example.organizadoracademico.domain.repository.IMateriaRepository

class AddMateriaUseCase(
    private val repository: IMateriaRepository
) {
    suspend operator fun invoke(materia: Materia): Result<Unit> {
        return try {
            repository.insertMateria(materia)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}