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
                _state.update { it.copy(materiaId = event.materiaId) }
            }

            is CamaraEvent.FotoTomada -> {
                _state.update { it.copy(lastPhotoUri = event.uri) }
            }

            is CamaraEvent.ContinuarConNota -> {
                // Solo activamos la navegación. No borramos la URI aquí.
                _state.update { it.copy(photoSaved = true) }
            }

            is CamaraEvent.ResetNavegacion -> {
                // IMPORTANTE: Solo ponemos photoSaved en false.
                // Mantenemos lastPhotoUri con valor para que la imagen siga visible
                // mientras ocurre la transición de pantallas y así evitar el parpadeo.
                _state.update { it.copy(photoSaved = false) }
            }

            is CamaraEvent.DescartarFoto -> {
                // Aquí sí borramos la foto porque el usuario explícitamente la rechazó.
                _state.update { it.copy(lastPhotoUri = null, photoSaved = false) }
            }

            is CamaraEvent.LimpiarTodo -> {
                // Úsalo al entrar a la pantalla (LaunchedEffect Unit) para limpiar basura anterior.
                _state.update { it.copy(lastPhotoUri = null, photoSaved = false, errorMessage = null) }
            }

            is CamaraEvent.ResetError -> {
                _state.update { it.copy(errorMessage = null) }
            }
            // Los eventos de permisos se manejarán en la UI
            else -> {}
        }
    }
}