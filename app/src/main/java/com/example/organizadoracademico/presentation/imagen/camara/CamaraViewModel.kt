package com.example.organizadoracademico.presentation.imagen.camara

import androidx.lifecycle.ViewModel
import com.example.organizadoracademico.hardware.camera.ImageSaver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CamaraViewModel(
    // El CameraManager se usará directamente en la UI, no aquí.
    private val imageSaver: ImageSaver
) : ViewModel() {

    private val _state = MutableStateFlow(CamaraState())
    val state: StateFlow<CamaraState> = _state.asStateFlow()

    fun onEvent(event: CamaraEvent) {
        when (event) {
            is CamaraEvent.Inicializar -> {
                // La materiaId se sigue necesitando para guardar la foto.
                _state.update { it.copy(materiaId = event.materiaId) }
            }
            is CamaraEvent.FotoTomada -> {
                _state.update { it.copy(lastPhotoUri = event.uri) }
            }
            is CamaraEvent.ContinuarConNota -> {
                // Lógica para guardar la foto (si es necesario) y navegar
                _state.update { it.copy(photoSaved = true) }
            }
            is CamaraEvent.DescartarFoto -> {
                _state.update { it.copy(lastPhotoUri = null) }
            }
            is CamaraEvent.ResetError -> {
                _state.update { it.copy(errorMessage = null) }
            }
            // Los eventos de permisos se manejarán en la UI
            else -> {}
        }
    }
}