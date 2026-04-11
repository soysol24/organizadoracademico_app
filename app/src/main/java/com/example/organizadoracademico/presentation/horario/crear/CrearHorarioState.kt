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
    val dias: List<String> = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", ),
    val horas: List<String> = listOf(
        "7:00", "8:00", "9:00", "10:00", "11:00", "12:00",
        "13:00", "14:00", "15:00", "16:00", "17:00", "18:00",
         ),
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