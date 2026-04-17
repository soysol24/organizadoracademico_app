package com.example.organizadoracademico.domain.usercase.horario

import com.example.organizadoracademico.domain.repository.IHorarioRepository

class ValidateHorarioTraslapeUseCase(
    private val repository: IHorarioRepository
) {
    suspend operator fun invoke(
        usuarioId: Int,
        dia: String,
        horaInicio: String,
        horaFin: String
    ): Boolean {
        return repository.existeTraslapeHorario(
            usuarioId = usuarioId,
            dia = dia,
            horaInicio = horaInicio,
            horaFin = horaFin
        )
    }
}

