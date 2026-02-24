package com.example.organizadoracademico.domain.usercase.imagen

import com.example.organizadoracademico.domain.repository.IImagenRepository

class DeleteImagenUseCase(
    private val repository: IImagenRepository
) {
    suspend operator fun invoke(id: Int): Result<Unit> {
        return try {
            repository.deleteImagen(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}