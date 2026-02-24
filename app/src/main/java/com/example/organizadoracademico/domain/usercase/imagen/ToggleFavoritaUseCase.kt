package com.example.organizadoracademico.domain.usercase.imagen

import com.example.organizadoracademico.domain.repository.IImagenRepository

class ToggleFavoritaUseCase(
    private val repository: IImagenRepository
) {
    suspend operator fun invoke(imagenId: Int, favorita: Boolean): Result<Unit> {
        return try {
            repository.toggleFavorita(imagenId, favorita)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}