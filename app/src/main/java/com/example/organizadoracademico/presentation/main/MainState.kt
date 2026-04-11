package com.example.organizadoracademico.presentation.main

data class MainState(
    val usuarioNombre: String = "Sol",
    val horariosHoy: List<HorarioCardData> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class HorarioCardData(
    val nombre: String,
    val profesor: String,
    val horaInicio: String,
    val horaFin: String,
    val color: String
)

sealed class MainEvent {
    object CargarDatos : MainEvent()
    object NavegarCrearHorario : MainEvent()
    object NavegarVerHorario : MainEvent()
    object NavegarMisMaterias : MainEvent()
    object NavegarPerfil : MainEvent()
    object NavegarAjustes : MainEvent()
}