package com.example.organizadoracademico.domain.usercase.usuario

class LogoutUseCase {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            // Aquí iría la lógica de cerrar sesión
            // Limpiar preferencias, etc.
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}