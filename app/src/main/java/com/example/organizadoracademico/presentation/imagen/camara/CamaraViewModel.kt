package com.example.organizadoracademico.presentation.imagen.camara

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organizadoracademico.hardware.camera.CameraManager
import com.example.organizadoracademico.hardware.camera.ImageSaver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CamaraViewModel(
    private val cameraManager: CameraManager,
    private val imageSaver: ImageSaver
) : ViewModel() {

    private val _state = MutableStateFlow(CamaraState())
    val state: StateFlow<CamaraState> = _state.asStateFlow()

    fun onEvent(event: CamaraEvent) {
        when (event) {
            is CamaraEvent.Inicializar -> {
                _state.update { it.copy(materiaId = event.materiaId) }
            }
            is CamaraEvent.TomarFoto -> {
                tomarFoto()
            }
            is CamaraEvent.FotoTomada -> {
                _state.update { it.copy(lastPhotoUri = event.uri, isTakingPhoto = false) }
            }
            is CamaraEvent.ContinuarConNota -> {
                _state.update { it.copy(photoSaved = true) }
            }
            is CamaraEvent.DescartarFoto -> {
                _state.update { it.copy(lastPhotoUri = null) }
            }
            is CamaraEvent.SolicitarPermiso -> {
                _state.update { it.copy(shouldShowPermissionDialog = true) }
            }
            is CamaraEvent.PermisoConcedido -> {
                _state.update { it.copy(hasCameraPermission = true, shouldShowPermissionDialog = false) }
            }
            is CamaraEvent.PermisoDenegado -> {
                _state.update { it.copy(shouldShowPermissionDialog = false) }
            }
            is CamaraEvent.ResetError -> {
                _state.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun tomarFoto() {
        _state.update { it.copy(isTakingPhoto = true, errorMessage = null) }

        cameraManager.takePhoto(
            onSuccess = { uri ->
                onEvent(CamaraEvent.FotoTomada(uri))
            },
            onError = { error ->
                _state.update { it.copy(isTakingPhoto = false, errorMessage = error) }
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        cameraManager.shutdown()
    }
}