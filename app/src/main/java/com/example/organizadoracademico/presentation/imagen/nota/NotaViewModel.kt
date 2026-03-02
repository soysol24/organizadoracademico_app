package com.example.organizadoracademico.presentation.imagen.nota

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organizadoracademico.domain.usercase.imagen.SaveImagenConNotaUseCase
import com.example.organizadoracademico.hardware.camera.ImageSaver
import com.example.organizadoracademico.hardware.vibration.VibratorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotaViewModel(
    private val saveImagenConNotaUseCase: SaveImagenConNotaUseCase,
    private val imageSaver: ImageSaver,
    private val vibratorManager: VibratorManager
) : ViewModel() {

    private val _state = MutableStateFlow(NotaState())
    val state: StateFlow<NotaState> = _state.asStateFlow()

    fun onEvent(event: NotaEvent) {
        when (event) {
            is NotaEvent.Inicializar -> {
                _state.update { it.copy(materiaId = event.materiaId, imageUri = event.imageUri) }
            }
            is NotaEvent.NotaCambio -> {
                if (event.nota.length <= NotaState.MAX_CHARS) {
                    _state.update { it.copy(nota = event.nota) }
                }
            }
            is NotaEvent.GuardarNota -> guardarNota(saltar = false)
            is NotaEvent.SaltarNota -> guardarNota(saltar = true)
            is NotaEvent.ResetError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun guardarNota(saltar: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val savedUri = imageSaver.saveImageToGallery(
                    imagePath = _state.value.imageUri,
                    materiaNombre = "Materia_${_state.value.materiaId}"
                )

                val notaTexto = if (saltar) null else _state.value.nota
                
                // Llamada directa a la función suspend
                saveImagenConNotaUseCase(materiaId = _state.value.materiaId, uri = savedUri, nota = notaTexto)

                // Si llegamos aquí, todo ha ido bien
                vibratorManager.vibrateSuccess()
                _state.update { it.copy(isLoading = false, isSaved = true) } // ¡Éxito!

            } catch (e: Exception) {
                vibratorManager.vibrateError()
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error desconocido"
                    )
                }
            }
        }
    }
}