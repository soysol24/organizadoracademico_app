package com.example.organizadoracademico.presentation.materia

import com.example.organizadoracademico.domain.model.Materia
import com.example.organizadoracademico.domain.model.Imagen

data class MisMateriasState(
    val materias: List<Materia> = emptyList(),
    val imagenesPorMateria: Map<Int, List<Imagen>> = emptyMap(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = ""
)

sealed class MisMateriasEvent {
    object CargarMaterias : MisMateriasEvent()
    data class SearchQueryChanged(val query: String) : MisMateriasEvent()
    data class SeleccionarMateria(val materiaId: Int) : MisMateriasEvent()
}