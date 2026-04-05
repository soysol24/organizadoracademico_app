package com.example.organizadoracademico.presentation.main

data class MainState(
    val usuarioNombre: String = "Sol",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class MainEvent {
    object CargarDatos : MainEvent()
    object NavegarCrearHorario : MainEvent()
    object NavegarVerHorario : MainEvent()
    object NavegarMisMaterias : MainEvent()
    object NavegarPerfil : MainEvent()
    object NavegarAjustes : MainEvent()
}