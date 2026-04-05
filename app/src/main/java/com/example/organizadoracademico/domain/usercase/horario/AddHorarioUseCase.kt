package com.example.organizadoracademico.domain.usercase.horario

import com.example.organizadoracademico.domain.model.Horario
import com.example.organizadoracademico.domain.repository.IHorarioRepository

class AddHorarioUseCase(
    private val repository: IHorarioRepository
) {
    suspend operator fun invoke(horario: Horario): Result<Unit> {
        return try {
            repository.insertHorario(horario)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}