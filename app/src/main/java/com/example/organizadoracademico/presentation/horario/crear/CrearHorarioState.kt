package com.example.organizadoracademico.presentation.horario.crear

import com.example.organizadoracademico.domain.model.Materia
import com.example.organizadoracademico.domain.model.Profesor

data class CrearHorarioState(
    // Listas de opciones
    val materias: List<Materia> = emptyList(),
    val profesores: List<Profesor> = emptyList(),

    // Valores seleccionados
    val materiaSeleccionada: Materia? = null,
    val profesorSeleccionado: Profesor? = null,
    val diaSeleccionado: String = "Lunes",
    val horaInicio: String = "8:00",
    val horaFin: String = "9:00",
    val colorSeleccionado: String = "Morado",

    // Estados
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,

    // Listas predefinidas
    val dias: List<String> = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"),
    val horas: List<String> = listOf(
        "8:00", "8:30", "9:00", "9:30", "10:00", "10:30",
        "11:00", "11:30", "12:00", "12:30", "13:00", "13:30", "14:00", "14:30",
        "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00", "18:30",
        "19:00"),
    val colores: List<String> = listOf("Morado", "Azul", "Verde", "Naranja", "Rojo", "Rosa")
)

sealed class CrearHorarioEvent {
    data class SeleccionarMateria(val materia: Materia) : CrearHorarioEvent()
    data class SeleccionarProfesor(val profesor: Profesor) : CrearHorarioEvent()
    data class SeleccionarDia(val dia: String) : CrearHorarioEvent()
    data class SeleccionarHoraInicio(val hora: String) : CrearHorarioEvent()
    data class SeleccionarHoraFin(val hora: String) : CrearHorarioEvent()
    data class SeleccionarColor(val color: String) : CrearHorarioEvent()
    object GuardarHorario : CrearHorarioEvent()
    object ResetSuccess : CrearHorarioEvent()
    object CargarDatosIniciales : CrearHorarioEvent()
}