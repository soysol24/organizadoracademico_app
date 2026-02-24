package com.example.organizadoracademico.presentation.imagen.galeria

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organizadoracademico.domain.model.Imagen
import com.example.organizadoracademico.domain.usercase.imagen.DeleteImagenUseCase
import com.example.organizadoracademico.domain.usercase.imagen.GetImagenesPorMateriaUseCase
import com.example.organizadoracademico.domain.usercase.materia.GetMateriasUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class GaleriaViewModel(
    private val getImagenesPorMateriaUseCase: GetImagenesPorMateriaUseCase,
    private val getMateriasUseCase: GetMateriasUseCase,
    private val deleteImagenUseCase: DeleteImagenUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GaleriaState())
    val state: StateFlow<GaleriaState> = _state.asStateFlow()

    fun onEvent(event: GaleriaEvent) {
        when (event) {
            is GaleriaEvent.CargarImagenes -> cargarImagenes(event.materiaId)
            is GaleriaEvent.EliminarImagen -> eliminarImagen(event.imagenId)
            is GaleriaEvent.ResetError -> {
                _state.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun cargarImagenes(materiaId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // Cargar información de la materia
                getMateriasUseCase().collect { materias ->
                    val materia = materias.find { it.id == materiaId }
                    _state.update { state ->
                        state.copy(materia = materia)
                    }
                }

                // Cargar imágenes de la materia
                getImagenesPorMateriaUseCase(materiaId).collect { imagenes ->
                    val agrupadas = agruparImagenesPorFecha(imagenes)
                    _state.update {
                        it.copy(
                            imagenes = imagenes,
                            imagenesAgrupadas = agrupadas,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar imágenes: ${e.message}"
                    )
                }
            }
        }
    }

    private fun eliminarImagen(imagenId: Int) {
        viewModelScope.launch {
            val result = deleteImagenUseCase.invoke(imagenId)

            result.onSuccess {
                // La imagen se eliminará de la lista cuando se recargue
            }.onFailure { exception ->
                _state.update {
                    it.copy(
                        errorMessage = exception.message ?: "Error al eliminar imagen"
                    )
                }
            }
        }
    }

    private fun agruparImagenesPorFecha(imagenes: List<Imagen>): Map<String, List<Imagen>> {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("es"))
        return imagenes.groupBy { imagen ->
            dateFormat.format(Date(imagen.fecha))
        }.toSortedMap(reverseOrder())
    }

    fun getFechaFormateada(fecha: Long): String {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("es"))
        return dateFormat.format(Date(fecha))
    }
}