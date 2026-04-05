package com.example.organizadoracademico.domain.usercase.horario

import com.example.organizadoracademico.domain.repository.IHorarioRepository

class DeleteHorarioUseCase(
    private val repository: IHorarioRepository
) {
    suspend operator fun invoke(id: Int): Result<Unit> {
        return try {
            repository.deleteHorario(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}