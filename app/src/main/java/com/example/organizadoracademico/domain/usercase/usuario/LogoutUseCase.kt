package com.example.organizadoracademico.domain.usercase.usuario

import com.example.organizadoracademico.domain.repository.IUsuarioRepository

class LogoutUseCase(
    private val repository: IUsuarioRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            repository.logout()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}