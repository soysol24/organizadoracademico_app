package com.example.organizadoracademico.domain.usercase.horario

import com.example.organizadoracademico.domain.model.Horario
import com.example.organizadoracademico.domain.repository.IHorarioRepository
import kotlinx.coroutines.flow.Flow

class GetHorariosUseCase(
    private val repository: IHorarioRepository
) {
    operator fun invoke(): Flow<List<Horario>> {
        return repository.getAllHorarios()
    }
}