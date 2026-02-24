package com.example.organizadoracademico.presentation.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organizadoracademico.domain.usercase.usuario.GetUsuarioUseCase
import com.example.organizadoracademico.domain.usercase.usuario.LogoutUseCase
import com.example.organizadoracademico.domain.usercase.materia.GetMateriasUseCase
import com.example.organizadoracademico.domain.usercase.horario.GetHorariosUseCase
import com.example.organizadoracademico.domain.usercase.imagen.GetImagenesPorMateriaUseCase
import com.example.organizadoracademico.hardware.vibration.VibratorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PerfilViewModel(
    private val getUsuarioUseCase: GetUsuarioUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getMateriasUseCase: GetMateriasUseCase,
    private val getHorariosUseCase: GetHorariosUseCase,
    private val getImagenesPorMateriaUseCase: GetImagenesPorMateriaUseCase,
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
                if (event.activada) {
                    vibratorManager.vibrateClick()
                }
                // Aquí guardarías la preferencia en DataStore
            }
            is PerfilEvent.ToggleSonido -> {
                _state.update { it.copy(sonidoActivado = event.activado) }
                // Aquí guardarías la preferencia en DataStore
            }
            is PerfilEvent.ToggleNotificaciones -> {
                _state.update { it.copy(notificacionesActivadas = event.activadas) }
                // Aquí guardarías la preferencia en DataStore
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

            try {
                // Cargar usuario (por defecto ID 1)
                val usuarioResult = getUsuarioUseCase.invoke(1)
                usuarioResult.onSuccess { usuario ->

                    // Contar materias
                    var totalMaterias = 0
                    getMateriasUseCase().collect { materias ->
                        totalMaterias = materias.size
                    }

                    // Contar horarios
                    var totalHorarios = 0
                    getHorariosUseCase().collect { horarios ->
                        totalHorarios = horarios.size
                    }

                    // Contar imágenes (simplificado)
                    var totalImagenes = 0
                    // Por ahora lo dejamos en 0

                    _state.update {
                        it.copy(
                            usuario = usuario,
                            totalMaterias = totalMaterias,
                            totalHorarios = totalHorarios,
                            totalImagenes = totalImagenes,
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
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error inesperado: ${e.message}"
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
                // La navegación se maneja en la UI
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