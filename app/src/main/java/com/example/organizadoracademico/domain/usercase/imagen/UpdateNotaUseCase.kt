package com.example.organizadoracademico.domain.usercase.imagen

import com.example.organizadoracademico.domain.repository.IImagenRepository

class UpdateNotaUseCase(
    private val repository: IImagenRepository
) {
    suspend operator fun invoke(id: Int, nota: String): Result<Unit> {
        return try {
            repository.updateNota(id, nota)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}