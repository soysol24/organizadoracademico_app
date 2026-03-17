package com.example.organizadoracademico.presentation.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            is PerfilEvent.CerrarSesion -> cerrarSesion()
            is PerfilEvent.ResetError -> {
                _state.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun cargarPerfil() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Usuario por defecto (ID 1)
            val result = getUsuarioUseCase.invoke(1)

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
                        errorMessage = "Error al cargar perfil: ${exception.message}"
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
                vibratorManager.vibrateSuccess()
                _state.update { it.copy(isLoggingOut = false) }
            }.onFailure { exception ->
                vibratorManager.vibrateError()
                _state.update {
                    it.copy(
                        isLoggingOut = false,
                        errorMessage = "Error al cerrar sesión: ${exception.message}"
                    )
                }
            }
        }
    }
}