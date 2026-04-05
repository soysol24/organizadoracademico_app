package com.example.organizadoracademico.domain.usercase.materia

import com.example.organizadoracademico.domain.model.Materia
import com.example.organizadoracademico.domain.repository.IMateriaRepository
import kotlinx.coroutines.flow.Flow

class GetMateriasUseCase(
    private val repository: IMateriaRepository
) {
    // CAMBIO: Ya no solicitamos el userId porque las materias son compartidas
    operator fun invoke(): Flow<List<Materia>> {
        // Llamamos al nuevo método global de la interfaz
        return repository.getAllMaterias()
    }
}