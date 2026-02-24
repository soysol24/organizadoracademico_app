package com.example.organizadoracademico.presentation.horario.ver

import com.example.organizadoracademico.domain.model.Horario
import com.example.organizadoracademico.domain.model.Materia
import com.example.organizadoracademico.domain.model.Profesor

data class VerHorarioState(
    val horarios: List<Horario> = emptyList(),
    val materias: Map<Int, Materia> = emptyMap(),
    val profesores: Map<Int, Profesor> = emptyMap(),
    val diasSemana: List<String> = listOf("LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB", "DOM"),
    val diaSeleccionado: String = "LUN",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class VerHorarioEvent {
    data class SeleccionarDia(val dia: String) : VerHorarioEvent()
    object CargarHorarios : VerHorarioEvent()
    data class EliminarHorario(val horarioId: Int) : VerHorarioEvent()
}