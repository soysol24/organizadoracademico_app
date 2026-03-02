package com.example.organizadoracademico.presentation.imagen.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organizadoracademico.domain.usercase.imagen.DeleteImagenUseCase
import com.example.organizadoracademico.domain.usercase.imagen.GetImagenUseCase
import com.example.organizadoracademico.domain.usercase.imagen.UpdateNotaUseCase
import com.example.organizadoracademico.domain.usercase.materia.GetMateriasUseCase
import com.example.organizadoracademico.hardware.vibration.VibratorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetalleImagenViewModel(
    private val getImagenUseCase: GetImagenUseCase,
    private val updateNotaUseCase: UpdateNotaUseCase,
    private val deleteImagenUseCase: DeleteImagenUseCase,
    private val getMateriasUseCase: GetMateriasUseCase, // <-- CASO DE USO AÑADIDO
    private val vibratorManager: VibratorManager
) : ViewModel() {

    private val _state = MutableStateFlow(DetalleImagenState())
    val state: StateFlow<DetalleImagenState> = _state.asStateFlow()

    fun onEvent(event: DetalleImagenEvent) {
        when (event) {
            is DetalleImagenEvent.CargarImagen -> cargarImagen(event.id)
            is DetalleImagenEvent.IniciarEdicion -> _state.update { it.copy(isEditando = true) }
            is DetalleImagenEvent.CancelarEdicion -> _state.update { it.copy(isEditando = false, nota = it.imagen?.nota ?: "") }
            is DetalleImagenEvent.NotaCambio -> _state.update { it.copy(nota = event.nota) }
            is DetalleImagenEvent.GuardarNota -> guardarNota()
            is DetalleImagenEvent.ToggleFavorita -> { /* Lógica futura */ }
            is DetalleImagenEvent.EliminarImagen -> eliminarImagen()
            is DetalleImagenEvent.ResetError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun cargarImagen(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val imagen = getImagenUseCase(id)
                if (imagen != null) {
                    val materia = getMateriasUseCase().first().find { it.id == imagen.materiaId }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            imagen = imagen,
                            nombreMateria = materia?.nombre ?: "Detalle",
                            nota = imagen.nota ?: ""
                        )
                    }
                } else {
                    throw Exception("Imagen no encontrada")
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    private fun guardarNota() {
        viewModelScope.launch {
            _state.value.imagen?.let {
                try {
                    updateNotaUseCase(it.id, _state.value.nota)
                    vibratorManager.vibrateSuccess()
                    _state.update { state ->
                        state.copy(
                            isEditando = false,
                            imagen = state.imagen?.copy(nota = _state.value.nota)
                        )
                    }
                } catch (e: Exception) {
                    vibratorManager.vibrateError()
                    _state.update { state ->
                        state.copy(errorMessage = "Error al guardar la nota: ${e.message}")
                    }
                }
            }
        }
    }

    private fun eliminarImagen() {
        viewModelScope.launch {
            _state.value.imagen?.let {
                try {
                    deleteImagenUseCase(it.id)
                    vibratorManager.vibrateSuccess()
                    _state.update { state -> state.copy(eliminado = true) }
                } catch (e: Exception) {
                    vibratorManager.vibrateError()
                    _state.update {
                        it.copy(errorMessage = "Error al eliminar la imagen: ${e.message}")
                    }
                }
            }
        }
    }
}