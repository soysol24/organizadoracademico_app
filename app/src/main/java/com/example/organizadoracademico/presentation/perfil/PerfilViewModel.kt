package com.example.organizadoracademico.presentation.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organizadoracademico.data.local.util.SessionManager
import com.example.organizadoracademico.domain.usercase.usuario.GetUsuarioUseCase
import com.example.organizadoracademico.domain.usercase.usuario.LogoutUseCase
import com.example.organizadoracademico.hardware.vibration.VibratorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PerfilViewModel(
    private val getUsuarioUseCase: GetUsuarioUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val sessionManager: SessionManager,
    private val vibratorManager: VibratorManager
) : ViewModel() {

    private val _state = MutableStateFlow(PerfilState())
    val state: StateFlow<PerfilState> = _state.asStateFlow()

    init {
        cargarPerfil()
    }

    fun onEvent(event: PerfilEvent) {
        when (event) {
            is PerfilEvent.CargarPerfil -> cargarPerfil()
            is PerfilEvent.ToggleVibracion -> {
                _state.update { it.copy(vibracionActivada = event.activada) }
                if (event.activada) vibratorManager.vibrateClick()
            }
            is PerfilEvent.ToggleSonido -> {
                _state.update { it.copy(sonidoActivado = event.activado) }
            }
            is PerfilEvent.ToggleNotificaciones -> {
                _state.update { it.copy(notificacionesActivadas = event.activadas) }
            }
            is PerfilEvent.CerrarSesion -> cerrarSesion()
            is PerfilEvent.ResetError -> {
                _state.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun cargarPerfil() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val userId = sessionManager.getUserId()
            if (userId == -1) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Usuario no encontrado"
                    )
                }
                return@launch
            }

            val result = getUsuarioUseCase.invoke(userId)

            result.onSuccess { usuario ->
                _state.update {
                    it.copy(
                        usuario = usuario,
                        isLoading = false
                    )
                }
            }.onFailure { exception ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.message
                    )
                }
            }
        }
    }

    private fun cerrarSesion() {
        viewModelScope.launch {
            _state.update { it.copy(isLoggingOut = true) }

            val result = logoutUseCase.invoke()

            result.onSuccess {
                sessionManager.logout()
                vibratorManager.vibrateSuccess()
                _state.update {
                    it.copy(
                        isLoggingOut = false,
                        usuario = null // LIMPIAMOS EL USUARIO PARA DISPARAR LA NAVEGACIÓN
                    )
                }
            }.onFailure { exception ->
                vibratorManager.vibrateError()
                _state.update {
                    it.copy(
                        isLoggingOut = false,
                        errorMessage = exception.message
                    )
                }
            }
        }
    }
}