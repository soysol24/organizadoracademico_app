package com.example.organizadoracademico.presentation.perfil

import com.example.organizadoracademico.domain.model.Usuario

data class PerfilState(
    val usuario: Usuario? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    // Configuraciones
    val vibracionActivada: Boolean = true,
    val sonidoActivado: Boolean = true,
    val notificacionesActivadas: Boolean = true,

    // Estadísticas (opcional)
    val totalMaterias: Int = 0,
    val totalHorarios: Int = 0,
    val totalImagenes: Int = 0,

    val isLoggingOut: Boolean = false
)

sealed class PerfilEvent {
    object CargarPerfil : PerfilEvent()
    data class ToggleVibracion(val activada: Boolean) : PerfilEvent()
    data class ToggleSonido(val activado: Boolean) : PerfilEvent()
    data class ToggleNotificaciones(val activadas: Boolean) : PerfilEvent()
    object CerrarSesion : PerfilEvent()
    object ResetError : PerfilEvent()
}