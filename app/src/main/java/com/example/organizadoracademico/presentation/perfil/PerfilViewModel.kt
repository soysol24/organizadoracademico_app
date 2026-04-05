package com.example.organizadoracademico.presentation.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organizadoracademico.data.local.util.SessionManager
import com.example.organizadoracademico.domain.usercase.usuario.GetUsuarioUseCase
import com.example.organizadoracademico.domain.usercase.usuario.LogoutUseCase
import com.example.organizadoracademico.domain.usercase.materia.GetMateriasUseCase
import com.example.organizadoracademico.domain.usercase.horario.GetHorariosUseCase
import com.example.organizadoracademico.domain.usercase.imagen.GetImagenesPorMateriaUseCase
import com.example.organizadoracademico.hardware.vibration.VibratorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PerfilViewModel(
    private val getUsuarioUseCase: GetUsuarioUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getMateriasUseCase: GetMateriasUseCase,
    private val getHorariosUseCase: GetHorariosUseCase,
    private val getImagenesPorMateriaUseCase: GetImagenesPorMateriaUseCase,
    private val vibratorManager: VibratorManager,
    private val sessionManager: SessionManager // Inyectado vía Koin
) : ViewModel() {

    private val _state = MutableStateFlow(PerfilState())
    val state: StateFlow<PerfilState> = _state.asStateFlow()

    // Obtenemos el ID real del usuario desde la sesión
    private val userId = sessionManager.getUserId()

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

            try {
                // 1. Cargamos los datos del usuario logueado
                val usuarioResult = getUsuarioUseCase.invoke(userId)

                usuarioResult.onSuccess { usuario ->
                    // 2. Contar materias (Globales)
                    val materias = getMateriasUseCase().first()

                    // 3. Contar horarios (Privados del usuario)
                    val horarios = getHorariosUseCase(userId).first()

                    // 4. Contar total de imágenes (Sumando fotos de cada materia del usuario)
                    var totalFotos = 0
                    materias.forEach { materia ->
                        val fotos = getImagenesPorMateriaUseCase(materia.id, userId).first()
                        totalFotos += fotos.size
                    }

                    _state.update {
                        it.copy(
                            usuario = usuario,
                            totalMaterias = materias.size,
                            totalHorarios = horarios.size,
                            totalImagenes = totalFotos,
                            isLoading = false
                        )
                    }
                }.onFailure { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Error al cargar datos de usuario: ${exception.message}"
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
                // SessionManager.clear() ya debería ser llamado dentro del LogoutUseCase
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