package com.example.organizadoracademico.presentation.perfil

import com.example.organizadoracademico.domain.model.Usuario

data class PerfilState(
    val usuario: Usuario? = null,
    val isLoading: Boolean = false,
    val isLoggingOut: Boolean = false,
    val errorMessage: String? = null
)

sealed class PerfilEvent {
    object CargarPerfil : PerfilEvent()
    object CerrarSesion : PerfilEvent()
    object ResetError : PerfilEvent()
}