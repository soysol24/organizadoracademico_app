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
                _state.update {
                    it.copy(
                        materiaId = event.materiaId,
                        imageUri = event.imageUri
                    )
                }
            }
            is NotaEvent.NotaCambio -> {
                val nota = event.nota.take(500) // Limitar a 500 caracteres
                _state.update {
                    it.copy(
                        nota = nota,
                        caracteresRestantes = 500 - nota.length
                    )
                }
            }
            is NotaEvent.GuardarNota -> {
                guardarNota()
            }
            is NotaEvent.SaltarNota -> {
                guardarNota(saltar = true)
            }
            is NotaEvent.ResetError -> {
                _state.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun guardarNota(saltar: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val notaTexto = if (saltar) null else _state.value.nota

            try {
                // Primero guardar la imagen en la galería
                imageSaver.saveImageToGallery(
                    imagePath = _state.value.imageUri,
                    materiaNombre = "Materia_${_state.value.materiaId}",
                    onSuccess = { savedUri ->
                        // Luego guardar en la base de datos
                        viewModelScope.launch {
                            val result = saveImagenConNotaUseCase.invoke(
                                materiaId = _state.value.materiaId,
                                uri = savedUri,
                                nota = notaTexto
                            )

                            result.onSuccess {
                                vibratorManager.vibrateSuccess()
                                _state.update {
                                    it.copy(
                                        isLoading = false,
                                        isSaved = true,
                                        errorMessage = null
                                    )
                                }
                            }.onFailure { exception ->
                                vibratorManager.vibrateError()
                                _state.update {
                                    it.copy(
                                        isLoading = false,
                                        errorMessage = "Error al guardar: ${exception.message}"
                                    )
                                }
                            }
                        }
                    },
                    onError = { error ->
                        vibratorManager.vibrateError()
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = error
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                vibratorManager.vibrateError()
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error inesperado: ${e.message}"
                    )
                }
            }
        }
    }
}