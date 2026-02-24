package com.example.organizadoracademico.domain.usercase.usuario

import com.example.organizadoracademico.domain.model.Usuario
import com.example.organizadoracademico.domain.repository.IUsuarioRepository

class GetUsuarioUseCase(
    private val repository: IUsuarioRepository
) {
    suspend operator fun invoke(id: Int): Result<Usuario?> {
        return try {
            // Por ahora retorna null, luego implementaremos
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}